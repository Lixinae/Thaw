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
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * Project :Thaw
 * Created by Narex on 31/10/2016.
 */
public class Server extends AbstractVerticle {

    private final List<Channel> channels;
    private final List<User> users;
    private final ThawLogger thawLogger;

    public Server() throws IOException {
        channels = new ArrayList<>();
        users = new ArrayList<>();
        thawLogger = new ThawLogger(false);
    }

    /**
     * @param enableLogger -> Enable the logger
     * @throws IOException -> If the logger can't find or create the file
     */
    public Server(boolean enableLogger) throws IOException {
        channels = new ArrayList<>();
        users = new ArrayList<>();
        thawLogger = new ThawLogger(enableLogger);// Enable or not the logs of the server
    }

    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        listOfRequest(router);

        // TEST ONLY //
        HumanUser test1 = UserFactory.createHumanUser("test1");
        HumanUser test2 = UserFactory.createHumanUser("test2");
        users.add(test1);
        users.add(test2);

        Channel defaul = ChannelFactory.createChannel(test1, "default");
        Channel channel = ChannelFactory.createChannel(test1, "Item 1");
        Channel channel2 = ChannelFactory.createChannel(test1, "Item 2");
//        Channel channel2 = ChannelFactory.createChannel(test2,"anotherChannel");
//        test1.joinChannel(channel);
//        test2.joinChannel(channel);
        test1.joinChannel(defaul);
        test2.joinChannel(defaul);

        defaul.addUserToChan(test2);

        channel2.addUserToChan(test2);

        channel.addUserToChan(test1);
        channel.addUserToChan(test2);

//        channel.addUserToChan(test1);
//        channel.addUserToChan(test2);

        channels.add(defaul);
        channels.add(channel);
        channels.add(channel2);
//        channels.add(channel2);


        // TEST //

        // Creation d'un serveur en https avec authentification
        // Exemple ici pour creer fichier jks : https://gist.github.com/InfoSec812/a45eb3b7ba9d4b2a9b94
        // C'est à se tirer une balle le truc
        // Sinon voila un exemple avec un fichier jks fourni
        // Fichier jks -> permet de stocker les certificat et autres trucs
//        HttpServer server =
//                vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyStoreOptions(
//                        new JksOptions().setPath("server-keystore.jks").setPassword("wibble")
//                ));
//
////        server.requestHandler(req -> {
////            req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
////        }).listen(8080);
//        server.requestHandler(router::accept).listen(8080);

// Plutot que de mettre le port de manière direct -> aller le chercher dans la configuration
//        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
//                result -> {
//                    if (result.succeeded()) {
//                        fut.complete();
//                    } else {
//                        fut.fail(result.cause());
//                    }
//                });
//        System.out.println("listen on port "+config().getInteger("http.port"));

        // otherwise serve static pages
        router.route().handler(StaticHandler.create());
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        System.out.println("listen on port 8080");
    }


    private void listOfRequest(Router router) {
        // route to JSON REST APIs

        ///////////////////////////////////
        // Remove after finishing test !!!
//        router.get("/api/testParam/:username").handler(this::testAjax);
//        router.get("/api/test").handler(this::testAjax2);
//        router.post("/api/testJson").handler(this::testAjax3);
        ///////////////////////////////////

        router.post("/api/createChannel").handler(this::createChannel);
        router.post("/api/deleteChannel").handler(this::deleteChannel);
        router.post("/api/connectToChannel").handler(this::connectToChannel);
        router.post("/api/sendMessage").handler(this::sendMessage);
        router.post("/api/getListMessageForChannel").handler(this::getListMessageForChannel);
        router.get("/api/getListChannel").handler(this::getListChannels);
        router.post("/api/getListUserForChannel").handler(this::getListUserForChannel);

    }

    private void getListMessageForChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "DEBUG " + "In getListMessageForChannel request");
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
        Optional<Channel> optChan = findChannelInList(channelName);
        if (optChan.isPresent()) {
            Channel channel = optChan.get();
            List<Message> messages = channel.getListMessage();
            answerToRequest(response, 200, messages);
        } else {
            answerToRequest(response, 400, "Channel : " + channelName + " doesn't exist");
        }
    }

    // Todo -> A tester
    private void createChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "DEBUG " + "In createChannel request");
//        System.out.println("DEBUG " + "In createChannel request");
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
        thawLogger.log(Level.INFO, "DEBUG " + newChannelName + " " + creatorName + " ");

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
            createAndAddChannel(response, newChannelName, creator);
        }

    }

    private void createAndAddChannel(HttpServerResponse response, String newChannelName, HumanUser creator) {
        Channel newChannel = ChannelFactory.createChannel(creator, newChannelName);
        channels.add(newChannel);
        creator.createChannel(newChannel);
        answerToRequest(response, 200, "Channel " + newChannelName + " successfully created");
    }

    private void deleteChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "DEBUG " + "In deleteChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeDeleteChannelRequest(response, json);
        }

    }

    private void analyzeDeleteChannelRequest(HttpServerResponse response, JsonObject json) {

    }

    // TODO -> A tester
    private void connectToChannel(RoutingContext routingContext) {
//        System.out.println("DEBUG " + "In connectToChannel request");
        thawLogger.log(Level.INFO, "DEBUG " + "In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            analyzeConnecToChannelRequest(response, json);
        }
    }

    //TODO : refactorer davantage le code (en evitant les Optional en parametres)
    private void analyzeConnecToChannelRequest(HttpServerResponse response, JsonObject json) {
        String oldChannelName = json.getString("oldChannelName");
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");
        thawLogger.log(Level.INFO, "DEBUG " + "\noldChannelName : " + oldChannelName + "\nuserName : " + userName + "\nnewChannelName : " + channelName);
//        System.out.println("DEBUG " + oldChannelName + " " + userName + " " + channelName);
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
            answerToRequest(response, 400, "Channel " + channelName + " does not exist");
        } else {
            Channel chan = optchannel.get();
            Optional<User> tmpUserInChan = chan.findUser(user);
            if (tmpUserInChan.isPresent()) {
                answerToRequest(response, 400, "User " + userName + " is already connected");
            } else {
                Optional<Channel> optChannelOld = findChannelInList(oldChannelName);
                if (!optChannelOld.isPresent()) {
                    answerToRequest(response, 400, "OldChannel " + oldChannelName + " does not exist");
                } else {
                    Channel oldChan = optChannelOld.get();
                    establishConnection(user, chan, oldChan);
                    answerToRequest(response, 200, " Successfully connected to channel " + chan.getName());
                }
            }
        }
    }

    private void establishConnection(User user, Channel chan, Channel oldChan) {
        user.quitChannel(oldChan);
        user.joinChannel(chan);
        thawLogger.log(Level.INFO, "DEBUG " + "User " + user.getName() + " connected to " + chan.getName() + "channel");
//        response.setStatusCode(200).end();
    }


    // TODO -> A tester
    private void getListUserForChannel(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "DEBUG " + "In getListUserForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            String channelName = json.getString("channelName");
            if (channelName.isEmpty()) {
                // Todo -> changer les code de retour en cas d'erruer
                answerToRequest(response, 400, "No channelName given");
                return;
            }
            Optional<Channel> channelOptional = findChannelInList(channelName);
            if (channelOptional.isPresent()) {
                List<String> tmp = channelOptional.get().getListUser().stream().map(User::getName).collect(Collectors.toList());
                answerToRequest(response, 200, tmp);
            } else {
                // Todo -> changer les code de retour en cas d'erruer
                answerToRequest(response, 400, "Channel " + channelName + " doesn't exist");
            }

        }
    }

    // TODO -> Fonctionne
    private void getListChannels(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "DEBUG " + "In getListChannels request");
        List<String> tmp = channels.stream().map(Channel::getChannelName).collect(Collectors.toList());
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(tmp));
    }

    // TODO -> A tester
    // Devrait fonctionner
    // Fonctionne à condition d'avoir ajouter l'utilisateur au channel courant :)
    private void sendMessage(RoutingContext routingContext) {
        thawLogger.log(Level.INFO, "DEBUG " + "In sendMessage request");
        JsonObject json = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format");
        } else {
            long date = System.currentTimeMillis();

            String message = json.getString("message");
            String userName = json.getString("username");
            String channelName = json.getString("channelName");

            Optional<Channel> channelOptional = findChannelInList(channelName);
            if (!channelOptional.isPresent()) {
                answerToRequest(response, 400, "Channel " + channelName + " does not exist");
                return;
            }
            Channel chan = channelOptional.get();

            // This should never happen, it's only matter of security
            Optional<User> optUsr = chan.findUserByName(userName);
            if (!optUsr.isPresent()) {
                answerToRequest(response, 400, "User " + userName + " does not exist");
                return;
            }
            User user = optUsr.get();
            Message mes = MessageFactory.createMessage(user, date, message);

            System.out.println(mes.getContent());

            user.sendMessage(chan, mes);
            // Todo : Analyser le message si un bot est connecté
//            chan.addMessageToQueue(mes);
            // TODO Stocker les information du message dans la base de donnée du channel

            // Recupère liste des message du channel
//            List<Message> messageListTmp = chan.getListMessage();
            // TODO Renvoyer la liste des messages correctement formaté pour l'affichage
            routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(mes));
        }
    }

    private void answerToRequest(HttpServerResponse response, int code, Object answer) {
        if (code == 200) {
            thawLogger.log(Level.INFO, "code " + code + " " + answer.toString());
        } else {
            thawLogger.log(Level.WARNING, "code " + code + " " + answer.toString());
        }
        response.setStatusCode(code)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(answer));
    }

    private Optional<User> findUserInServerUserList(String userName) {
        return users.stream()
                .filter(u -> u.getName().contentEquals(userName))
                .findFirst();
//        for (User u : users) {
//            if (u.getName().contentEquals(userName)) {
//                return Optional.of(u);
//            }
//        }
//        return Optional.empty();
    }

    private Optional<Channel> findChannelInList(String channelName) {
        return channels.stream()
                .filter(c -> c.getChannelName().contentEquals(channelName))
                .findFirst();
//        for (Channel c : channels) {
//            if (c.getChannelName().contentEquals(channelName)) {
//                return Optional.of(c);
//            }
//        }
//        return Optional.empty();
    }


//    // TODO -> Les 3 tests Fonctionne
//    // Penser à les enlever pour le rendu !!!!
//    private void testAjax(RoutingContext routingContext) {
////        System.out.println("niaaaaa");
////        String username = Json.decodeValue(routingContext.getBodyAsString(),String.class);
//        String username = routingContext.request().getParam("username");
//        System.out.println(username);
//        List<String> test = new ArrayList<>();
//        test.add("blork");
//        test.add("yoooofjugt");
//        test.add("hehefgjhuefh");
//
//        routingContext.response()
//                .putHeader("content-type", "application/json")
//                .end(Json.encodePrettily(test));
//    }
//
//    private void testAjax2(RoutingContext routingContext) {
//        Map<Integer, String> testMap = new HashMap<>();
//
//        testMap.put(1, "Bmurk");
//        testMap.put(2, "Randim");
//        testMap.put(10, "Textalacon");
//        System.out.println("moi");
//        routingContext.response()
//                .putHeader("content-type", "application/json")
//                .end(Json.encodePrettily(testMap));
//    }
//
//    private void testAjax3(RoutingContext routingContext) {
//        JsonObject json = routingContext.getBodyAsJson();
//        System.out.println(json);
//        List<String> test = new ArrayList<>();
//        test.add("This is");
//        test.add("a Json");
//        test.add("Test");
//
////        Message mes = new Message(new HumanUser("Blork"), 1123, "Mon super message");
//        routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(test));
////        routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(mes));
//    }
}