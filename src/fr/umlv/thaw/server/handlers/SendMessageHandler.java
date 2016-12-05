package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class SendMessageHandler {
    // Fonctionne
    // TODO : Traitement des messages en cas de bot et stockage dans la base de donnée
    public static void sendMessage(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In sendMessage request");
        JsonObject json = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeSendMessageRequest(response, session, json, thawLogger, channels);
        }
    }

    private static void analyzeSendMessageRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        long date = System.currentTimeMillis();

        String message = json.getString("message");
        String userName = json.getString("username");
        String channelName = json.getString("channelName");

        if (Tools.verifyEmptyOrNull(message, userName, channelName)) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<User> optUser = Tools.checkIfUserIsConnectedAndAuthorized(session, response, thawLogger);
        if (!optUser.isPresent()) {
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
}
