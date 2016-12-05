package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GetListChannelsHandler {
    // Fonctionne
    public static void getListChannels(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In getListChannels request");
        List<String> tmp = channels.stream().map(Channel::getChannelName).collect(Collectors.toList());
        HttpServerResponse response = routingContext.response();
        Tools.answerToRequest(response, 200, tmp, thawLogger);
    }
}
