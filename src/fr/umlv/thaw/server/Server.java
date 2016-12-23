package fr.umlv.thaw.server;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.database.Database;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * One of ours principal classes.
 * This class represent our server for
 * our Thaw Application.
 */
public class Server extends AbstractVerticle {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private final static int maxUploadSize = 50 * MB;
    private final List<Channel> channels;
    private final List<HumanUser> authorizedHumanUsers;
    private final List<User> connectedUsers;
    private final ThawLogger thawLogger;
    private final Database database;

    private final boolean ssl;

    /**
     * @param database     The database in which we will makes our jobs
     * @throws IOException If the logger can't find or create the file
     */
    public Server(Database database) throws IOException {
        this.database = Objects.requireNonNull(database);
        this.ssl = true;
        thawLogger = new ThawLogger(true);// Enable or not the logs of the server
        connectedUsers = new ArrayList<>();
        channels = new ArrayList<>();// database.getChannelList();//We retrieve the channels that already existed
        authorizedHumanUsers = new ArrayList<>();// We retrieve the registered user
    }


    @Override
    /*
    *   Because of the routines that we must set, we cannot
    * produce less line for start without creating more
    * methods that could throws exception.
    *
    * */
    public void start(Future<Void> fut) {
        initializeDatabase();
        // We need to keep at least one super user to create the default channel & have an account to use the test-api
        // Because we block it in javascript, this user can only be used in the test api.
        String hashPassword = Tools.toSHA256("password2");
        HumanUser superUser = HumanUserFactory.createHumanUser("#SuperUser", hashPassword);
        createLogin(superUser);
        Channel general = ChannelFactory.createChannel(superUser, "general");
        createChannelTable(general);
        thawLogger.log(Level.INFO, "Loading database data ");
        if (loadAuthorizedHumanUsers()) {
            return;
        }
        loadChannelList();
        loadUserForChannels();
        general.addUserToChan(superUser);
        thawLogger.log(Level.INFO, "Database loading is done");
/////////////////////////////////////////////////////////////////////////////////
        final int bindPort = 8080;
        Router router = Router.router(vertx);
        allRoutes(router);
        if (ssl) {
            // SSL requested, start a SSL HTTP server.
            startSSLServer(fut, bindPort, router);
        } else {
            // No SSL requested, start a non-SSL HTTP server.
            startNonSSLServer(fut, bindPort, router);
        }
    }

    private void allRoutes(Router router) {
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create().setBodyLimit(maxUploadSize));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        listOfRequest(router);
        router.route().handler(StaticHandler.create());
    }

    /*
    *   Because we must initialize correctly each routine and
    * log the potential warnings, we could not write less lines.
    * */
    private void loadUserForChannels() {
        thawLogger.log(Level.INFO, "Binding each user to his channel");
        //We add each users to every existing Channel
        for (Channel chan : channels) {
            for (User usr : authorizedHumanUsers) {
                try {
                    database.addUserToChan(chan.getChannelName(), usr.getName(), chan.getCreatorName());
                } catch (SQLException sql) {
                    thawLogger.log(Level.WARNING, "Problem adding : " + usr.getName());
                }
            }
        }
    }

    private void loadChannelList() {
        thawLogger.log(Level.INFO, "Loading channel list");
        channels.addAll(database.getChannelList());
    }

    private boolean loadAuthorizedHumanUsers() {
        try {
            thawLogger.log(Level.INFO, "Loading authorized HumanUser list");
            authorizedHumanUsers.addAll(database.getAllUsersList());
        } catch (SQLException e) {
            // No human authorized -> Nobody can connect, so crash the server.
            return true;
        }
        return false;
    }

    private void createChannelTable(Channel general) {
        try {
            database.createChannelTable(general.getChannelName(), "#SuperUser");
        } catch (SQLException sql) {
            thawLogger.log(Level.INFO, "Channels already registered");
        }
    }

    private void initializeDatabase() {
        try {
            thawLogger.log(Level.INFO, "Initializing database");
            database.initializeDB();
        } catch (SQLException sql) {
            //database already set up correctly
            thawLogger.log(Level.INFO, "Database already set up");
        }
    }

    /*To distinct each possible cases and avoid to throw too much
    * SQLException, we must make a try catch and log what happened
    * */
    private void createLogin(HumanUser superUser) {
        try {
            database.createLogin(superUser);
        } catch (SQLException sql) {
            //login already exists
            thawLogger.log(Level.WARNING, "User " + superUser.getName() + " already in database created");
        }
        authorizedHumanUsers.add(superUser);
    }

    private void startNonSSLServer(Future<Void> fut, int bindPort, Router router) {
        vertx.createHttpServer().requestHandler(router::accept).listen(bindPort);
        thawLogger.log(Level.INFO, "Non SSL Web server now listening");
        fut.complete();
    }

    /*
    *   Because of the log and the necessity to secure the connection
    * we cannot write less line and must also tell the user when we need
    * that the certificate is created.
    * */
    private void startSSLServer(Future<Void> fut, int bindPort, Router router) {
        vertx.executeBlocking(future -> {
                    HttpServerOptions httpOpts = new HttpServerOptions();
                    Path path = Paths.get("./config/webserver/.keystore.jks");
                    if (Files.notExists(path)) {
                        System.err.println("Please generate a certificate using the following command before : ");
                        System.err.println("keytool -genkey -alias localhost -keyalg RSA -keystore .keystore.jks -validity 365 -keysize 2048");
                        System.err.println("then stock it into ./config/webserver/");
                        System.exit(0);
                    }
                    httpOpts.setKeyStoreOptions(new JksOptions().setPath("./config/webserver/.keystore.jks").setPassword("password"));
                    httpOpts.setSsl(true);
                    future.complete(httpOpts);
                },
                (AsyncResult<HttpServerOptions> result) -> {
                    if (!result.failed()) {
                        vertx.createHttpServer(result.result()).requestHandler(router::accept).listen(bindPort);
                        thawLogger.log(Level.INFO, "SSL Web server now listening on port :" + bindPort);
                        fut.complete();
                    }
                });
    }


    /*
    * Because we've got more than 8 requests that can be performed
    * we can't write less lines.
    * */
    private void listOfRequest(Router router) {

        // No need of post or get for these
        router.route("/api/connectToServer").handler(routingContext -> Handlers.connectToServerHandle(routingContext, thawLogger, authorizedHumanUsers, connectedUsers, channels));
        router.route("/api/private/disconnectFromServer").handler(routingContext -> Handlers.disconnectFromServerHandle(routingContext, thawLogger, channels, connectedUsers));
        router.route("/api/createAccount").handler(routingContext -> Handlers.createAccountHandle(routingContext, thawLogger, authorizedHumanUsers, database));
        router.route("/api/private/*").handler(routingContext -> Handlers.securityCheckHandle(routingContext, thawLogger, authorizedHumanUsers, connectedUsers));


        // Post & get requests
        router.post("/api/private/addChannel").handler(routingContext -> Handlers.addChannelHandle(routingContext, thawLogger, channels, database));
        router.post("/api/private/deleteChannel").handler(routingContext -> Handlers.deleteChannelHandle(routingContext, thawLogger, channels, database));
        router.post("/api/private/connectToChannel").handler(routingContext -> Handlers.connectToChannelHandle(routingContext, thawLogger, channels));
        router.post("/api/private/sendMessage").handler(routingContext -> Handlers.sendMessageHandle(routingContext, thawLogger, channels, database));
        router.post("/api/private/getListMessageForChannel").handler(routingContext -> Handlers.getListMessageForChannelHandle(routingContext, thawLogger, channels, database));
        router.post("/api/private/getListUserForChannel").handler(routingContext -> Handlers.getListUserForChannelHandle(routingContext, thawLogger, channels));
        router.get("/api/private/getListChannel").handler(routingContext -> Handlers.getListChannelHandle(routingContext, thawLogger, channels));

    }
}