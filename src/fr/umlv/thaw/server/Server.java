package fr.umlv.thaw.server;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Project :Thaw
 * Created by Narex on 31/10/2016.
 */
public class Server extends AbstractVerticle {

    private final List<Channel> channels = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);

        //
//        router.route().handler(CookieHandler.create());
//        router.route().handler(SessionHandler
//                .create(LocalSessionStore.create(vertx))
//                .setCookieHttpOnlyFlag(true)
//                .setCookieSecureFlag(true)
//        );
//
//        router.route().handler(routingContext -> {
//
//            Session session = routingContext.session();
//
//            Integer cnt = session.get("hitcount");
//            cnt = (cnt == null ? 0 : cnt) + 1;
//
//            session.put("hitcount", cnt);
//
//            routingContext.response().end("Hitcount: " + cnt);
//        });
        router.route().handler(BodyHandler.create());

        listOfRequest(router);

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
        router.get("/api/testParam/:username").handler(this::testAjax);
        router.get("/api/test").handler(this::testAjax2);
        router.post("/api/testJson").handler(this::testAjax3);
        ///////////////////////////////////

        router.post("/api/createChannel").handler(this::createChannel);
        router.post("/api/connectToChannel").handler(this::connectToChannel);
        router.post("/api/sendMessage").handler(this::sendMessage);
        router.get("/api/getListChannel").handler(this::getListChannels);
        router.get("/api/getListUserForChannel").handler(this::getListUserForChannel);

    }

    private void createChannel(RoutingContext routingContext) {

    }

    // TODO -> A tester
    private void connectToChannel(RoutingContext routingContext) {
        System.out.println("In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            errorWithMessage(response, 400, "Wrong Json format");
        } else {
            analyzeRequest(response, json);
        }
    }

    //TODO : refactorer davantage le code (en evitant les Optional en parametres)
    private void analyzeRequest(HttpServerResponse response, JsonObject json) {
        String oldChannelName = json.getString("oldChannelName");
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");
        System.out.println(oldChannelName + " " + userName + " " + channelName);
        Optional<Channel> optchannel = findChannelInList(channelName);
        Optional<User> optuser = findUserInList(userName);
        User user;
        if (!optuser.isPresent()) {
            user = new HumanUser(userName);
            users.add(user);
        } else {
            user = optuser.get();
        }
        if (!optchannel.isPresent()) {
            errorWithMessage(response, 400, "Channel " + channelName + " does not exist");
        } else {
            Channel chan = optchannel.get();
            Optional<User> tmpUserInChan = findUserInListFromChan(chan.getListUser(), userName);
            if (tmpUserInChan.isPresent()) {
                errorWithMessage(response, 400, "User " + userName + " is already connected");
            } else {
                Optional<Channel> optChannelOld = findChannelInList(oldChannelName);
                if (!optChannelOld.isPresent()) {
                    errorWithMessage(response, 400, "OldChannel " + oldChannelName + " does not exist");
                } else {
                    Channel oldChan = optChannelOld.get();
                    establishConnection(response, user, chan, oldChan);
                }
            }
        }
    }

    private void establishConnection(HttpServerResponse response, User user, Channel chan, Channel oldChan) {
        oldChan.removeUserFromChan(user);
        // Only add a user to a channel if we can remove him from the old channel
        chan.addUserToChan(user);
        System.out.println("User connected to right channel");
        response.setStatusCode(200).end();
    }


    // TODO -> A tester
    private void getListUserForChannel(RoutingContext routingContext) {
        System.out.println("In getListUser request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            String channelName = json.getString("channelName");

            if (channelName.isEmpty()) {
                // Todo -> changer les code de retour en cas d'erruer
                response.setStatusCode(400).end();
                return;
            }
            Optional<Channel> channelOptional = findChannelInList(channelName);
            if (channelOptional.isPresent()) {
                List<String> tmp = channelOptional.get().getListUser().stream().map(User::getName).collect(Collectors.toList());
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(Json.encodePrettily(tmp));
            } else {
                // Todo -> changer les code de retour en cas d'erruer
                response.setStatusCode(400).end();
            }

        }
    }

    // TODO -> Fonctionne
    private void getListChannels(RoutingContext routingContext) {
        System.out.println("In getListChannels request");
        List<String> tmp = channels.stream().map(Channel::getChannelName).collect(Collectors.toList());
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(tmp));
    }

    // TODO -> A tester
    // Devrait fonctionner
    // Fonctionne à condition d'avoir ajouter l'utilisateur au channel courant :)
    private void sendMessage(RoutingContext routingContext) {
        System.out.println("In sendMessage request");
        JsonObject json = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
        if (json == null) {
            errorWithMessage(response, 400, "Wrong Json format");
        } else {
            long date = System.currentTimeMillis();

            String message = json.getString("message");
            String userName = json.getString("username");
            String channelName = json.getString("channelName");

            Optional<Channel> channelOptional = findChannelInList(channelName);
            if (!channelOptional.isPresent()) {
                errorWithMessage(response, 400, "Channel " + channelName + " does not exist");
                return;
            }
            Channel chan = channelOptional.get();
            // This should never happen, it's only matter of security
            Optional<User> optUsr = findUserInListFromChan(chan.getListUser(), userName);
            if (!optUsr.isPresent()) {
                errorWithMessage(response, 400, "User " + userName + " does not exist");
                return;
            }
            User user = optUsr.get();
            Message mes = new Message(user, date, message);

            System.out.println(mes.getContent());
            // Pas forcement utile de renvoyer le message reçu
            // Renvoyer quelque chose d'autre mais je ne sais pas quoi
            // TODO Stocker les information du message dans la base de donnée du channel
            routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(mes));
        }
    }

    private void errorWithMessage(HttpServerResponse response, int code, String s) {
        response.setStatusCode(code).end();
        System.err.println(s);
    }

    private Optional<User> findUserInListFromChan(List<User> listUser, String userName) {
        for (User u : listUser) {
//            System.out.println(u);
            if (u.getName().contentEquals(userName)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    private Optional<User> findUserInList(String userName) {
        for (User u : users) {
//            System.out.println(u);
            if (u.getName().contentEquals(userName)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    // TODO -> A tester
    private Optional<Channel> findChannelInList(String channelName) {
        for (Channel c : channels) {
            if (c.getChannelName().contentEquals(channelName)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }


    // TODO -> Les 3 tests Fonctionne
    // Penser à les enlever pour le rendu !!!!
    private void testAjax(RoutingContext routingContext) {
//        System.out.println("niaaaaa");
//        String username = Json.decodeValue(routingContext.getBodyAsString(),String.class);
        String username = routingContext.request().getParam("username");
        System.out.println(username);
        List<String> test = new ArrayList<>();
        test.add("blork");
        test.add("yoooofjugt");
        test.add("hehefgjhuefh");

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(test));
    }

    private void testAjax2(RoutingContext routingContext) {
        Map<Integer, String> testMap = new HashMap<>();

        testMap.put(1, "Bmurk");
        testMap.put(2, "Randim");
        testMap.put(10, "Textalacon");
        System.out.println("moi");
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(testMap));
    }

    private void testAjax3(RoutingContext routingContext) {
        JsonObject json = routingContext.getBodyAsJson();
        System.out.println(json);
        List<String> test = new ArrayList<>();
        test.add("This is");
        test.add("a Json");
        test.add("Test");

//        Message mes = new Message(new HumanUser("Blork"), 1123, "Mon super message");
        routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(test));
//        routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(mes));
    }
}