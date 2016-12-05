package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.logging.Level;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class DelChannelHandler {

    public static void deleteChannel(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In deleteChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeDeleteChannelRequest(response, json);
        }
    }

    private static void analyzeDeleteChannelRequest(HttpServerResponse response, JsonObject json) {
        // TODO : Deconnecter tout les utilisateur du channel avant sa destruction et les
        // remettre sur le channel "default"
    }

}
