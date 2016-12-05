package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GetListUserForChannelHandler {
    // Fonctionne
    public static void getListUserForChannel(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In getListUserForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            analyzegetListUserForChannelRequest(response, session, json, thawLogger, channels);
        }
    }

    private static void analyzegetListUserForChannelRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");
        if (!securityCheckGetListUserForChannel(response, channelName, userName, thawLogger)) {
            return;
        }
        Optional<User> optUser = Tools.checkIfUserIsConnectedAndAuthorized(session, response, thawLogger);
        if (!optUser.isPresent()) {
            return;
        }
        Optional<Channel> channelOptional = Tools.findChannelInList(channels, channelName);
        if (channelOptional.isPresent()) {
            List<String> tmp = channelOptional.get().getListUser().stream().map(User::getName).collect(Collectors.toList());
            Tools.answerToRequest(response, 200, tmp, thawLogger);
        } else {
            Tools.answerToRequest(response, 400, "Channel:" + channelName + " doesn't exist", thawLogger);
        }
    }

    private static boolean securityCheckGetListUserForChannel(HttpServerResponse response, String channelName, String userName, ThawLogger thawLogger) {
        if (Tools.verifyEmptyOrNull(channelName, userName)) {
            Tools.answerToRequest(response, 400, "No channelName or userName given", thawLogger);
            return false;
        }
        return true;
    }
}
