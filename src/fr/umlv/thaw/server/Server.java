package fr.umlv.thaw.server;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.UserFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * Project :Thaw
 * Created by Narex on 31/10/2016.
 */
public class Server extends AbstractVerticle {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private final static int maxUploadSize = 50 * MB;
    private final List<Channel> channels;
    private final List<User> users;
    private final ThawLogger thawLogger;

    public Server() throws IOException {
        channels = new ArrayList<>();
        users = new ArrayList<>();
        thawLogger = new ThawLogger(false);
    }

    /**
     * @param enableLogger Enable the logger
     * @throws IOException If the logger can't find or create the file
     */
    public Server(boolean enableLogger) throws IOException {
        channels = new ArrayList<>();
        users = new ArrayList<>();
        thawLogger = new ThawLogger(enableLogger);// Enable or not the logs of the server
    }

    @Override
    public void start(Future<Void> fut) throws Exception {


        // TEST ONLY //
        HumanUser superUser = UserFactory.createHumanUser("superUser");
        HumanUser test2 = UserFactory.createHumanUser("test2");
        users.add(superUser);
        users.add(test2);

        Channel defaul = ChannelFactory.createChannel(superUser, "default");
        Channel channel = ChannelFactory.createChannel(superUser, "Item 1");
        Channel channel2 = ChannelFactory.createChannel(superUser, "Item 2");

        superUser.joinChannel(defaul);
        test2.joinChannel(defaul);

        defaul.addUserToChan(test2);

        channel2.addUserToChan(test2);

        channel.addUserToChan(superUser);
        channel.addUserToChan(test2);
        channels.add(defaul);
        channels.add(channel);
        channels.add(channel2);
//        channels.add(channel2);
        // TEST //

//        Router router = Router.router(vertx);
//        listOfRequest(router);
//        router.route().handler(BodyHandler.create().setBodyLimit(maxUploadSize));
        // otherwise serve static pages
//        router.route().handler(StaticHandler.create());
//        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
//        System.out.println("listen on port 8080");
//        fut.complete();
/////////////////////////////////////////////////////////////////////////////////
        final int bindPort = 8080;
        final boolean ssl = true;
        Router router2 = Router.router(vertx);
        router2.route().handler(BodyHandler.create().setBodyLimit(maxUploadSize));
        listOfRequest(router2);
        router2.route().handler(StaticHandler.create());
        if (ssl) {
            vertx.executeBlocking(future -> {
                        HttpServerOptions httpOpts = new HttpServerOptions();
                        generateKeyPairAndCertificate(fut);
                        httpOpts.setKeyStoreOptions(new JksOptions().setPath("./config/webserver/.keystore.jks").setPassword("password"));
                        httpOpts.setSsl(true);
                        future.complete(httpOpts);
                    },
                    (AsyncResult<HttpServerOptions> result) -> {
                        if (!result.failed()) {
                            vertx.createHttpServer(result.result()).requestHandler(router2::accept).listen(bindPort);
                            thawLogger.log(Level.INFO, "SSL Web server now listening on port :" + bindPort);
                            fut.complete();
                        }
                    });
        } else {
            // No SSL requested, start a non-SSL HTTP server.
            vertx.createHttpServer().requestHandler(router2::accept).listen(bindPort);
            thawLogger.log(Level.INFO, "Web server now listening");
            fut.complete();
        }
    }

    private void generateKeyPairAndCertificate(Future<Void> fut) {
        try {
            // Generate a self-signed key pair and certificate.
            KeyStore store = KeyStore.getInstance("JKS");
            store.load(null, null);
            CertAndKeyGen keypair = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
            X500Name x500Name = new X500Name("localhost", "IT", "unknown", "unknown", "unknown", "unknown");
            keypair.generate(1024);
            PrivateKey privKey = keypair.getPrivateKey();
            X509Certificate[] chain = new X509Certificate[1];
            chain[0] = keypair.getSelfCertificate(x500Name, new Date(), (long) 365 * 24 * 60 * 60);
            store.setKeyEntry("selfsigned", privKey, "password".toCharArray(), chain);
            try (FileOutputStream outputStream = new FileOutputStream("./config/webserver/.keystore.jks")) {
                store.store(outputStream, "password".toCharArray());
            }
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | InvalidKeyException | SignatureException ex) {
            thawLogger.log(Level.SEVERE, "Failed to generate a self-signed cert and other SSL configuration methods failed.");
            fut.fail(ex);
        }
    }

    private void listOfRequest(Router router) {
        // route to JSON REST APIs
        router.post("/api/addChannel").handler(this::addChannel);
        router.post("/api/deleteChannel").handler(this::deleteChannel);
        router.post("/api/connectToChannel").handler(this::connectToChannel);
        router.post("/api/sendMessage").handler(this::sendMessage);
        router.post("/api/getListMessageForChannel").handler(this::getListMessageForChannel);
        router.get("/api/getListChannel").handler(this::getListChannels);
        router.post("/api/getListUserForChannel").handler(this::getListUserForChannel);

    }

    // Fonctionne
    private void addChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In addChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeCreateChannelRequest(response, json);
        }
    }

    private void analyzeCreateChannelRequest(HttpServerResponse response, JsonObject json) {
        String newChannelName = json.getString("newChannelName");
        String creatorName = json.getString("creatorName");
        thawLogger.log(Level.INFO, newChannelName + " " + creatorName + " ");
        if (verifyEmptyOrNull(newChannelName, creatorName)) {
            answerToRequest(response, 400, "Wrong JSON input");
            return;
        }
        Optional<Channel> optChannel = findChannelInList(newChannelName);
        if (optChannel.isPresent()) {
            answerToRequest(response, 400, "Channel " + newChannelName + " already exists");
        } else {
            Optional<User> optUser = findUserInServerUserList(creatorName);
            User tmpUser;  // new HumanUser(creatorName);
            HumanUser creator;
            if (optUser.isPresent()) {
                tmpUser = optUser.get();
                if (tmpUser.isUserBot()) {
                    answerToRequest(response, 400, "Bots can't create channels ! Bot name = " + creatorName);
                    return;
                }
                creator = (HumanUser) tmpUser; // Todo Moche -> changer plus tard
            } else {
                // Ne devrait jamais arriver en utilisation normal du server
                // Un utilisateur sera toujours connecté au serveur lors de la demande de creation de channel
                creator = UserFactory.createHumanUser(creatorName);
                users.add(creator);
            }
            createAndAddChannel(newChannelName, creator);
            answerToRequest(response, 200, "Channel " + newChannelName + " successfully created");
        }
    }

    private void createAndAddChannel(String newChannelName, HumanUser creator) {
        Channel newChannel = ChannelFactory.createChannel(creator, newChannelName);
        channels.add(newChannel);
        creator.addChannel(newChannel);
    }

    private void deleteChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In deleteChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeDeleteChannelRequest(response, json);
        }
    }

    private void analyzeDeleteChannelRequest(HttpServerResponse response, JsonObject json) {
        // TODO : Deconnecter tout les utilisateur du channel avant sa destruction et les
        // remettre sur le channel "default"
    }

    // Fonctionne
    private void connectToChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeConnecToChannelRequest(response, json);
        }
    }

    //TODO : refactoriser davantage le code
    private void analyzeConnecToChannelRequest(HttpServerResponse response, JsonObject json) {
        String oldChannelName = json.getString("oldChannelName");
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");
        if (verifyEmptyOrNull(oldChannelName, channelName, userName)) {
            answerToRequest(response, 400, "Wrong JSON input");
            return;
        }
        Optional<Channel> optchannel = findChannelInList(channelName);
        Optional<User> optuser = findUserInServerUserList(userName);
        User user;
        if (!optuser.isPresent()) {
            user = UserFactory.createHumanUser(userName);
            users.add(user);
        } else {
            user = optuser.get();
        }
        if (!optchannel.isPresent()) {
            answerToRequest(response, 400, "Channel :" + channelName + " does not exist");
        } else {
            Channel chan = optchannel.get();
            Optional<User> tmpUserInChan = chan.findUser(user);
            if (tmpUserInChan.isPresent()) {
                answerToRequest(response, 400, "User :" + user + " is already connected");
            } else {
                Optional<Channel> optChannelOld = findChannelInList(oldChannelName);
                if (!optChannelOld.isPresent()) {
                    answerToRequest(response, 400, "OldChannel " + oldChannelName + " does not exist");
                } else {
                    Channel oldChan = optChannelOld.get();
                    establishConnection(user, chan, oldChan);
                    String answer = "User :" + user + " successfully quit channel :'" + oldChannelName + '\'' + " and connected to channel :'" + channelName + '\'';
                    answerToRequest(response, 200, answer);
                }
            }
        }
    }

    private void establishConnection(User user, Channel chan, Channel oldChan) {
        user.quitChannel(oldChan);
        user.joinChannel(chan);
    }

    // Fonctionne
    // TODO : Traitement des messages en cas de bot et stockage dans la base de donnée
    private void sendMessage(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In sendMessage request");
        JsonObject json = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeSendMessageRequest(response, json);
        }
    }

    private void analyzeSendMessageRequest(HttpServerResponse response, JsonObject json) {
        long date = System.currentTimeMillis();

        String message = json.getString("message");
        String userName = json.getString("username");
        String channelName = json.getString("channelName");

        if (verifyEmptyOrNull(message, userName, channelName)) {
            answerToRequest(response, 400, "Wrong JSON input");
            return;
        }

        Optional<Channel> channelOptional = findChannelInList(channelName);
        if (!channelOptional.isPresent()) {
            answerToRequest(response, 400, "Channel: '" + channelName + "' doesn't exist");
            return;
        }
        Channel chan = channelOptional.get();

        // This should never happen, it's only matter of security
        Optional<User> optUsr = chan.findUserByName(userName);
        if (!optUsr.isPresent()) {
            answerToRequest(response, 400, "User: '" + userName + "' doesn't exist");
            return;
        }
        User user = optUsr.get();
        Message mes = MessageFactory.createMessage(user, date, message);

        user.sendMessage(chan, mes);
        // Todo : Analyser le message si un bot est connecté

        // TODO Stocker les information du message dans la base de donnée du channel

        // Recupère liste des message du channel
//            List<Message> messageListTmp = chan.getListMessage();
        // TODO Renvoyer la liste des messages correctement formaté pour l'affichage
        answerToRequest(response, 200, "Message: " + mes + " sent correctly to channel '" + channelName + '\'');
    }

    // Fonctionne
    private void getListMessageForChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In getListMessageForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeGetListMessageForChannelRequest(response, json);
        }
    }

    private void analyzeGetListMessageForChannelRequest(HttpServerResponse response, JsonObject json) {
        String channelName = json.getString("channelName");
        Integer numberOfMessageWanted = json.getInteger("numberOfMessage");
        if (verifyEmptyOrNull(channelName)) {
            answerToRequest(response, 400, "No channelName given");
            return;
        }
        if (numberOfMessageWanted < 1) {
            answerToRequest(response, 400, "Number Of Message must be > 0 !");
            return;
        }
        Optional<Channel> optChan = findChannelInList(channelName);
        if (optChan.isPresent()) {
            Channel channel = optChan.get();
            List<Message> tmpMess = channel.getListMessage();
            List<Message> returnListMessage = tmpMess.subList(Math.max(tmpMess.size() - numberOfMessageWanted, 0), tmpMess.size());
            answerToRequest(response, 200, returnListMessage);
        } else {
            answerToRequest(response, 400, "Channel: " + channelName + " doesn't exist");
        }
    }

    // Fonctionne
    private void getListUserForChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In getListUserForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            String channelName = json.getString("channelName");
            if (verifyEmptyOrNull(channelName)) {
                answerToRequest(response, 400, "No channelName given");
                return;
            }
            Optional<Channel> channelOptional = findChannelInList(channelName);
            if (channelOptional.isPresent()) {
                List<String> tmp = channelOptional.get().getListUser().stream().map(User::getName).collect(Collectors.toList());
                answerToRequest(response, 200, tmp);
            } else {
                answerToRequest(response, 400, "Channel:" + channelName + " doesn't exist");
            }

        }
    }

    // Fonctionne
    private void getListChannels(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "In getListChannels request");
        List<String> tmp = channels.stream().map(Channel::getChannelName).collect(Collectors.toList());
        HttpServerResponse response = routingContext.response();
        answerToRequest(response, 200, tmp);
    }


    private void answerToRequest(HttpServerResponse response, int code, Object answer) {
        String tmp = Json.encodePrettily(answer);
        if (code == 200) {
            thawLogger.log(Level.INFO, "code: " + code + "\nanswer: " + tmp);
        } else {
            thawLogger.log(Level.WARNING, "code: " + code + "\nanswer: " + tmp);
        }
        response.setStatusCode(code)
                .putHeader("content-type", "application/json")
                .end(tmp);
    }

    private Optional<User> findUserInServerUserList(String userName) {
        if (verifyEmptyOrNull(userName)) {
            return Optional.empty();
        }
        return users.stream()
                .filter(u -> u.getName().contentEquals(userName))
                .findFirst();
    }

    private Optional<Channel> findChannelInList(String channelName) {
        if (verifyEmptyOrNull(channelName)) {
            return Optional.empty();
        }
        return channels.stream()
                .filter(c -> c.getChannelName().contentEquals(channelName))
                .findFirst();
    }

    private boolean verifyEmptyOrNull(String... strings) {
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}