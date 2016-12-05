package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

//TODO
public class DisconnectFromServerHandler {

    public static void disconnectFromServer(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        routingContext.clearUser();
        routingContext.response().putHeader("location", "/").setStatusCode(302).end();
    }

    private static void analyzeDisconnectFromServerRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {


    }
}
