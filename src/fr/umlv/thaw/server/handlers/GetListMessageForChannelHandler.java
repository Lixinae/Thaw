package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class GetListMessageForChannelHandler {
    // Fonctionne
    public static void getListMessageForChannel(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In getListMessageForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeGetListMessageForChannelRequest(response, json, thawLogger, channels);
        }
    }

    private static void analyzeGetListMessageForChannelRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        String channelName = json.getString("channelName");
        Integer numberOfMessageWanted = json.getInteger("numberOfMessage");
        if (!securityCheckGetListMessageForChannel(response, channelName, numberOfMessageWanted, thawLogger)) {
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

    private static boolean securityCheckGetListMessageForChannel(HttpServerResponse response, String channelName, Integer numberOfMessageWanted, ThawLogger thawLogger) {
        if (Tools.verifyEmptyOrNull(channelName)) {
            Tools.answerToRequest(response, 400, "No channelName given", thawLogger);
            return false;
        }
        if (numberOfMessageWanted < 1) {
            Tools.answerToRequest(response, 400, "Number Of Message must be > 0 !", thawLogger);
            return false;
        }
        return true;
    }
}
