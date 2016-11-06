package fr.umlv.thaw.server;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelImpl;
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


        // ONLY FOR TESTTING !!!!!! //
        // Fonctionne bien
        HumanUser a = new HumanUser("Narex");
        Channel c = new ChannelImpl(a, "monSuperChan1");
        c.addUserToChan(a);
        Channel c2 = new ChannelImpl(a, "monSuperChan2");
        Channel c3 = new ChannelImpl(a, "monSuperChan3");
        Channel c4 = new ChannelImpl(a, "monSuperChan4");
        channels.add(c);
        channels.add(c2);
        channels.add(c3);
        channels.add(c4);
        System.out.println(channels);
//        channels.get(1).addMessageToQueue(a,120,"truc");
        // END OF TEST !!!!!!!!!!! //

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

//    private void getAllDBs(RoutingContext routingContext) {
//        routingContext.response()
//                .putHeader("content-type", "application/json")
//                .end(List.of("bd1", "bd2").stream().map(Json::encodePrettily).collect(Collectors.joining(", ", "[", "]")));
//    }
//
//    private void getARecord(RoutingContext routingContext) {
//        HttpServerResponse response = routingContext.response();
//        HttpServerRequest request = routingContext.request();
//        String name = Objects.requireNonNull(request.getParam("name"));
//        int id = Integer.parseInt(request.getParam("id"));
//        if (name.isEmpty() || id < 0) {
//            response.setStatusCode(404).end();
//            return;
//        }
//        routingContext.response()
//                .putHeader("content-type", "application/json")
//                .end(Json.encodePrettily(Map.of("id", "" + id, "name", name)));
//    }

    private void listOfRequest(Router router) {
        // route to JSON REST APIs
//        router.get("/all").handler(this::getAllDBs);
//        router.get("/get/:name/:id").handler(this::getARecord);

        ///////////////////////////////////
        // Remove after finishing test !!!
        router.get("/api/testParam/:username").handler(this::testAjax);
        router.get("/api/test").handler(this::testAjax2);
        router.post("/api/testJson").handler(this::testAjax3);
        ///////////////////////////////////

        router.post("/api/connectToChannel").handler(this::connectToChannel);
        router.post("/api/sendMessage").handler(this::sendMessage);
        router.get("/api/getListChannel").handler(this::getListChannels);
        router.get("/api/getListUserForChannel").handler(this::getListUserForChannel);

    }

    // TODO
    private void connectToChannel(RoutingContext routingContext) {
        System.out.println("In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            response.setStatusCode(400).end();
        } else {
            String currentChannelName = json.getString("currentChannelName");
            String channelName = json.getString("channelName");
            String userName = json.getString("username");
            // Check si l'utilisateur est déjà connecté au chan auquel il veut se connecter
            // TODO
//            Optional<Channel> currentchannelOptional = findChannelInList(currentChannelName);
//            Optional<Channel> channelOptional = findChannelInList(channelName);
//            User user = new HumanUser(userName);
//            if (currentchannelOptional.isPresent() && channelOptional.isPresent()){
//                if (channelOptional.get().checkIfUserIsConnected(user)){
//
//                }
//            }
//
//
//            if(channelOptional.isPresent()){
//                Optional<User> userOptional = findUserInList(channelOptional.get().getListUser(),userName);
//                if (userOptional.isPresent()){
//                    response.setStatusCode(200).end();
//                    return;
//                }
//                User user = new HumanUser(userName); // -> Peut etre bancale, à voir
////                channels.get(findChannelIndex(channelName)).addUserToChan(user);
//                channelOptional.get().addUserToChan(user);
//
//
//            }else{
//                // Todo -> changer les code de retour en cas d'erruer
//                response.setStatusCode(400).end();
//            }
        }
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
                response.setStatusCode(404).end();
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
                response.setStatusCode(404).end();
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
            routingContext.response().setStatusCode(400).end();
        } else {
            long date = System.currentTimeMillis();

            String message = json.getString("message");
            String userName = json.getString("username");
            String channelName = json.getString("channelName");

            Optional<Channel> channelOptional = findChannelInList(channelName);
            if (!channelOptional.isPresent()) {
                response.setStatusCode(400).end();
                return;
            }
            Channel chan = channelOptional.get();
            // This should never happen, it's only matter of security
            Optional<User> optUsr = findUserInList(chan.getListUser(), userName);
            if (!optUsr.isPresent()) {
                response.setStatusCode(400).end();
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

    private Optional<User> findUserInList(List<User> userList, String userName) {
        for (User u : userList) {
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


//    private int findChannelIndex(String channelName) {
//        int i = 0;
//        for (Channel c : channels) {
//            if (c.getChannelName().contentEquals(channelName)) {
//                return i;
//            }
//            i++;
//        }
//        return -1;
//    }

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