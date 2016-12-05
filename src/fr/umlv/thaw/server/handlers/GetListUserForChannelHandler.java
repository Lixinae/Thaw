package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GetListUserForChannelHandler {
    // Fonctionne
    public static void getListUserForChannel(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In getListUserForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            analyzegetListUserForChannelRequest(response, json, thawLogger, channels, users);
        }
    }

    private static void analyzegetListUserForChannelRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");
        if (!securityCheckGetListUserForChannel(response, channelName, userName, thawLogger, users)) {
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

    private static boolean securityCheckGetListUserForChannel(HttpServerResponse response, String channelName, String userName, ThawLogger thawLogger, List<User> users) {
        if (Tools.verifyEmptyOrNull(channelName, userName)) {
            Tools.answerToRequest(response, 400, "No channelName or userName given", thawLogger);
            return false;
        }
        if (!isUserConnected(userName, users)) {
            Tools.answerToRequest(response, 400, "User " + userName + " is not connected to server", thawLogger);
            return false;
        }
        return true;
    }

    private static boolean isUserConnected(String userName, List<User> users) {
        return Tools.findUserInServerUserList(users, userName).isPresent();
    }
}
