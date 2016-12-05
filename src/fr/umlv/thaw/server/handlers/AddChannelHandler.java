package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class AddChannelHandler {

    public static void create(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In addChannel request");
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
        } else {
            analyzeCreateChannelRequest(session, response, json, thawLogger, channels, users);
        }
    }

    // Todo ajouter le truc avec la session
    private static void analyzeCreateChannelRequest(Session session, HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        String newChannelName = json.getString("newChannelName");
        String creatorName = json.getString("creatorName");
        thawLogger.log(Level.INFO, newChannelName + " " + creatorName + " ");
        if (Tools.verifyEmptyOrNull(newChannelName, creatorName)) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> optChannel = Tools.findChannelInList(channels, newChannelName);
        if (optChannel.isPresent()) {
            Tools.answerToRequest(response, 400, "Channel " + newChannelName + " already exists", thawLogger);
        } else {
            User user = session.get("user");
            if (user.isUserBot()) {
                Tools.answerToRequest(response, 400, "Bots can't create channels ! Bot name = " + creatorName, thawLogger);
                return;
            }
            HumanUser creator = (HumanUser) user; // Todo Moche -> changer plus tard
            createAndAddChannel(newChannelName, creator, channels);
            Tools.answerToRequest(response, 200, "Channel " + newChannelName + " successfully created", thawLogger);
        }
    }


    private static void createAndAddChannel(String newChannelName, HumanUser creator, List<Channel> channels) {
        Channel newChannel = ChannelFactory.createChannel(creator, newChannelName);
        channels.add(newChannel);
        creator.addChannel(newChannel);
    }

}
