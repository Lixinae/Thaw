package fr.umlv.thaw.server;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.user.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.*;


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
        router.route().handler(StaticHandler.create());
        listOfRequest(router);
        // otherwise serve static pages


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

        router.get("/api/testParam/:username").handler(this::testAjax);
        router.get("/api/test").handler(this::testAjax2);
        router.post("/api/testJson").handler(this::testAjax3);

        router.post("/api/sendMessage").handler(this::sendMessage);
        router.get("/api/getListChannel").handler(this::getListChannels);
        router.get("/api/getListUserForChannel/:channelName").handler(this::getListUserForChannel);

    }


    // TODO -> A tester
    private void getListUserForChannel(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();
        String channelName = Objects.requireNonNull(request.getParam("channelName"));
        if (channelName.isEmpty()) {
            response.setStatusCode(404).end();
            return;
        }
        int index = findChannelIndex(channelName);
        if (index == -1) {
            response.setStatusCode(404).end();
            return;
        }
        Channel chan = channels.get(index);
        List<User> tmp = chan.getListUser();
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(tmp));
    }

    // TODO -> A tester
    private int findChannelIndex(String channelName) {
        int i = 0;
        for (Channel c : channels) {
            if (c.getChannelName().contentEquals(channelName)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    // TODO -> A tester
    private void getListChannels(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(channels));
    }

    // TODO -> A tester
    private void sendMessage(RoutingContext routingContext) {
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
//            long date = System.currentTimeMillis();
//
//            String message = json.getString("message");
//            String userName = json.getString("username");
//            String channelName = json.getString("channel");
////            Channel chan = ; //TODO Faire quelque chose comme "channels.get(nomChan)
//            User user = ; //TODO Idem avec user
//            Message mes = new Message(user,date,message);
//            routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(mes));
        }
    }

    // TODO -> Fonctionne
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
        routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(test));
    }
}