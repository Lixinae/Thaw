package fr.umlv.thaw.server;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;
import fr.umlv.thaw.user.humanUser.HumanUserImpl;
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
import java.util.logging.Level;


/**
 * Project :Thaw
 * Created by Narex on 31/10/2016.
 */
public class Server extends AbstractVerticle {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private final static int maxUploadSize = 50 * MB;
    private final List<Channel> channels;
    private final List<HumanUser> authorizedHumanUsers;
    private final ThawLogger thawLogger;

    private final boolean ssl;

    public Server() throws IOException {
        channels = new ArrayList<>();
        authorizedHumanUsers = new ArrayList<>();
        thawLogger = new ThawLogger(false);
        this.ssl = false;
    }

    /**
     * @param enableLogger Enable the logger
     * @param ssl          Enable ssl
     * @throws IOException If the logger can't find or create the file
     */
    public Server(boolean enableLogger, boolean ssl) throws IOException {
        channels = new ArrayList<>();
        authorizedHumanUsers = new ArrayList<>(); // Need to construct authorized list
        thawLogger = new ThawLogger(enableLogger);// Enable or not the logs of the server
        this.ssl = ssl;
    }


    @Override
    public void start(Future<Void> fut) throws Exception {
        // TEST ONLY //
        String hashPassword = Tools.toSHA256("password");
        String hashPassword2 = Tools.toSHA256("password2");

        HumanUserImpl superUser = HumanUserFactory.createHumanUser("superUser", hashPassword);
        HumanUserImpl test2 = HumanUserFactory.createHumanUser("test2", hashPassword2);
        authorizedHumanUsers.add(superUser);
        authorizedHumanUsers.add(test2);
        Channel defaul = ChannelFactory.createChannel(superUser, "default");
        Channel channel = ChannelFactory.createChannel(superUser, "Item 1");
        Channel channel2 = ChannelFactory.createChannel(superUser, "Item 2");

        Message mes = MessageFactory.createMessage(superUser, 10, "1er lessage");
        Message mes1 = MessageFactory.createMessage(superUser, 20, "2e message");
        Message mes2 = MessageFactory.createMessage(superUser, 30, "3e message");
        defaul.addMessageToQueue(mes);
        defaul.addMessageToQueue(mes1);
        defaul.addMessageToQueue(mes2);

        superUser.joinChannel(defaul);
        test2.joinChannel(defaul);

        channels.add(defaul);
        channels.add(channel);
        channels.add(channel2);

//        AuthProvider authProvider = AuthOptions
//        JsonObject authInfo = new JsonObject().put("username", "tim").put("password", "mypassword");
//
//        authProvider.authenticate(authInfo, res -> {
//            if (res.succeeded()) {
//
//                io.vertx.ext.auth.User user = res.result();
//
//                System.out.println("User " + user.principal() + " is now authenticated");
//
//            } else {
//                res.cause().printStackTrace();
//            }
//        });
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
        Router router = Router.router(vertx);
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create().setBodyLimit(maxUploadSize));

        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));


        listOfRequest(router);
        router.route().handler(StaticHandler.create());
        if (ssl) {
            startSSLserver(fut, bindPort, router);
        } else {
            // No SSL requested, start a non-SSL HTTP server.
            startNonSSLserver(fut, bindPort, router);
        }
    }

    private void startNonSSLserver(Future<Void> fut, int bindPort, Router router) {
        vertx.createHttpServer().requestHandler(router::accept).listen(bindPort);
        thawLogger.log(Level.INFO, "Web server now listening");
        fut.complete();
    }

    private void startSSLserver(Future<Void> fut, int bindPort, Router router) {
        vertx.executeBlocking(future -> {
                    HttpServerOptions httpOpts = new HttpServerOptions();
                    generateKeyPairAndCertificate(fut);
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

    // All requests that the server can use
    private void listOfRequest(Router router) {

        // No need of post or get for these
        router.route("/api/connectToServer").handler(routingContext -> Handlers.connectToServerHandle(routingContext, thawLogger, authorizedHumanUsers));
        router.route("/api/private/disconnectFromServer").handler(routingContext -> Handlers.disconnectFromServerHandle(routingContext, thawLogger));
        router.route("/api/createAccount").handler(routingContext -> Handlers.createAccountHandle(routingContext, thawLogger, authorizedHumanUsers));
        router.route("/api/private/*").handler(routingContext -> Handlers.securityCheckHandle(routingContext, thawLogger, authorizedHumanUsers));


        // Post & get requests
        router.post("/api/private/addChannel").handler(routingContext -> Handlers.addChannelHandle(routingContext, thawLogger, channels));
        router.post("/api/private/deleteChannel").handler(routingContext -> Handlers.deleteChannelHandle(routingContext, thawLogger, channels));
        router.post("/api/private/connectToChannel").handler(routingContext -> Handlers.connectToChannelHandle(routingContext, thawLogger, channels));
        router.post("/api/private/sendMessage").handler(routingContext -> Handlers.sendMessageHandle(routingContext, thawLogger, channels));
        router.post("/api/private/getListMessageForChannel").handler(routingContext -> Handlers.getListMessageForChannelHandle(routingContext, thawLogger, channels));
        router.post("/api/private/getListUserForChannel").handler(routingContext -> Handlers.getListUserForChannelHandle(routingContext, thawLogger, channels));
        router.get("/api/private/getListChannel").handler(routingContext -> Handlers.getListChannelHandle(routingContext, thawLogger, channels));

    }
}