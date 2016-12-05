package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class SendMessageHandler {
    // Fonctionne
    // TODO : Traitement des messages en cas de bot et stockage dans la base de donnée
    public static void sendMessage(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In sendMessage request");
        JsonObject json = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeSendMessageRequest(response, json, thawLogger, channels, users);
        }
    }

    private static void analyzeSendMessageRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        long date = System.currentTimeMillis();

        String message = json.getString("message");
        String userName = json.getString("username");
        String channelName = json.getString("channelName");

        if (Tools.verifyEmptyOrNull(message, userName, channelName)) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        if (!isUserConnected(userName, users)) {
            Tools.answerToRequest(response, 400, "User " + userName + " is not connected to server", thawLogger);
            return;
        }

        Optional<Channel> channelOptional = Tools.findChannelInList(channels, channelName);
        if (!channelOptional.isPresent()) {
            Tools.answerToRequest(response, 400, "Channel: '" + channelName + "' doesn't exist", thawLogger);
            return;
        }
        Channel chan = channelOptional.get();

        // This should never happen, it's only matter of security
        Optional<User> optUsr = chan.findUserByName(userName);
        if (!optUsr.isPresent()) {
            Tools.answerToRequest(response, 400, "User: '" + userName + "' doesn't exist", thawLogger);
            return;
        }
        User user = optUsr.get();
        Message mes = MessageFactory.createMessage(user, date, message);

        user.sendMessage(chan, mes);
        // Todo : Analyser le message si un bot est connecté

        // TODO Stocker les information du message dans la base de donnée du channel

        // Recupère liste des message du channel
//            List<Message> messageListTmp = chan.getListMessage();
        // TODO Renvoyer la liste des messages correctement formaté pour l'affichage
        Tools.answerToRequest(response, 200, "Message: " + mes + " sent correctly to channel '" + channelName + '\'', thawLogger);
    }

    private static boolean isUserConnected(String userName, List<User> users) {
        return Tools.findUserInServerUserList(users, userName).isPresent();
    }
}
