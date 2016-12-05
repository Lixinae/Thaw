package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class GetListMessageForChannelHandler {
    // Fonctionne
    public static void getListMessageForChannel(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In getListMessageForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeGetListMessageForChannelRequest(response, json, thawLogger, channels, users);
        }
    }

    private static void analyzeGetListMessageForChannelRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        String channelName = json.getString("channelName");
        Integer numberOfMessageWanted = json.getInteger("numberOfMessage");
        String userName = json.getString("userName");
        if (!securityCheckGetListMessageForChannel(response, channelName, numberOfMessageWanted, userName, thawLogger, users)) {
            return;
        }
        Optional<Channel> optChan = Tools.findChannelInList(channels, channelName);
        if (optChan.isPresent()) {
            Channel channel = optChan.get();
            List<Message> tmpMess = channel.getListMessage();
            List<Message> returnListMessage = tmpMess.subList(Math.max(tmpMess.size() - numberOfMessageWanted, 0), tmpMess.size());
            Tools.answerToRequest(response, 200, returnListMessage, thawLogger);
        } else {
            Tools.answerToRequest(response, 400, "Channel: " + channelName + " doesn't exist", thawLogger);
        }
    }

    private static boolean securityCheckGetListMessageForChannel(HttpServerResponse response, String channelName, Integer numberOfMessageWanted, String userName, ThawLogger thawLogger, List<User> users) {
        if (Tools.verifyEmptyOrNull(channelName)) {
            Tools.answerToRequest(response, 400, "No channelName given", thawLogger);
            return false;
        }
        if (!isUserConnected(userName, users)) {
            Tools.answerToRequest(response, 400, "User " + userName + " is not connected to server", thawLogger);
            return false;
        }
        if (numberOfMessageWanted < 1) {
            Tools.answerToRequest(response, 400, "Number Of Message must be > 0 !", thawLogger);
            return false;
        }
        return true;
    }

    private static boolean isUserConnected(String userName, List<User> users) {
        return Tools.findUserInServerUserList(users, userName).isPresent();
    }
}
