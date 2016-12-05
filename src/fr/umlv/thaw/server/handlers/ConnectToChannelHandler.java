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


public class ConnectToChannelHandler {
    public static void connectToChannel(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeConnecToChannelRequest(response, json, thawLogger, channels, users);
        }
    }

    //TODO : refactoriser davantage le code
    private static void analyzeConnecToChannelRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        String oldChannelName = json.getString("oldChannelName");
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");

        if (Tools.verifyEmptyOrNull(oldChannelName, channelName, userName)) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> optchannel = Tools.findChannelInList(channels, channelName);
        Optional<User> optuser = Tools.findUserInServerUserList(users, userName);
        User user;
        if (!optuser.isPresent()) {
            Tools.answerToRequest(response, 400, "User " + userName + " is not connected to server", thawLogger);
            return;
        } else {
            user = optuser.get();
        }
        if (!optchannel.isPresent()) {
            Tools.answerToRequest(response, 400, "Channel :" + channelName + " does not exist", thawLogger);
        } else {
            Channel chan = optchannel.get();
            Optional<User> tmpUserInChan = chan.findUser(user);
            if (tmpUserInChan.isPresent()) {
                Tools.answerToRequest(response, 400, "User :" + user + " is already connected", thawLogger);
            } else {
                Optional<Channel> optChannelOld = Tools.findChannelInList(channels, oldChannelName);
                if (!optChannelOld.isPresent()) {
                    Tools.answerToRequest(response, 400, "OldChannel " + oldChannelName + " does not exist", thawLogger);
                } else {
                    Channel oldChan = optChannelOld.get();
                    establishConnection(user, chan, oldChan);
                    String answer = "User :" + user + " successfully quit channel :'" + oldChannelName + '\'' + " and connected to channel :'" + channelName + '\'';
                    Tools.answerToRequest(response, 200, answer, thawLogger);
                }
            }
        }
    }

    private static void establishConnection(User user, Channel chan, Channel oldChan) {
        user.quitChannel(oldChan);
        user.joinChannel(chan);
    }
}
