package fr.umlv.thaw.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Project :Thaw
 * Created by Narex on 31/10/2016.
 */
public class Server extends AbstractVerticle {
    @Override
    public void start() {
        Router router = Router.router(vertx);

        // route to JSON REST APIs
        router.get("/all").handler(this::getAllDBs);
        router.get("/get/:name/:id").handler(this::getARecord);

        // otherwise serve static pages
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        System.out.println("listen on port 8080");
    }

    private void getAllDBs(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(List.of("bd1", "bd2").stream().map(Json::encodePrettily).collect(Collectors.joining(", ", "[", "]")));
    }

    private void getARecord(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();
        String name = Objects.requireNonNull(request.getParam("name"));
        int id = Integer.parseInt(request.getParam("id"));
        if (name.isEmpty() || id < 0) {
            response.setStatusCode(404).end();
            return;
        }
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(Map.of("id", "" + id, "name", name)));
    }
}