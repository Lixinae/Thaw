package fr.umlv.thaw.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;


/**
 * Project :Thaw
 * Created by Narex on 31/10/2016.
 */
public class Server extends AbstractVerticle {
    @Override
    public void start() {
        Router router = Router.router(vertx);

        // route to JSON REST APIs
//        router.get("/all").handler(this::getAllDBs);
//        router.get("/get/:name/:id").handler(this::getARecord);
        System.out.println(router.getRoutes());
        listOfRequest(router);
        System.out.println(router.getRoutes());
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
        router.post("/test/:username").handler(this::testAjax);
        router.post("/sendMessage").handler(this::sendMessage);
        router.get("/getListChannel").handler(this::getListChannels);

    }

    private void getListChannels(RoutingContext routingContext) {
//        routingContext.response()
//                .putHeader("content-type", "application/json")
//                .end(Json.encodePrettily());
    }

    // TODO
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

    private void testAjax(RoutingContext routingContext) {
//        System.out.println("niaaaaa");
//        String username = Json.decodeValue(routingContext.getBodyAsString(),String.class);
        String username = routingContext.request().getParam("username");
        System.out.println(username);
    }
}