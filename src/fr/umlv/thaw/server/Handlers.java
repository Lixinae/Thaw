package fr.umlv.thaw.server;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Project :Thaw
 * Created by Narex on 09/12/2016.
 */
class Handlers {

    // Order of handlers is same as the order of usage in the server


    /*##############################################################*/
    /////////////////// Connect to server Handler ///////////////////
    /*##############################################################*/
    static void connectToServerHandle(RoutingContext routingContext, ThawLogger thawLogger, final List<User> authorizedUsers) {
        thawLogger.log(Level.INFO, "In connectToServer request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();

        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        }
        if (session == null) {
            Tools.answerToRequest(response, 400, "No session", thawLogger);
        } else {
            analyzeConnecToServerRequest(session, response, json, thawLogger, authorizedUsers);
        }
    }

    private static void analyzeConnecToServerRequest(Session session, HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<User> authorizedUsers) {
        String userName = json.getString("userName");
        String password = json.getString("password");
        byte[] passwordHash;
        try {
            passwordHash = Tools.hashToSha256(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        byte[] finalPasswordHash = passwordHash;
        for (User u : authorizedUsers) {
            if (u.getName().contentEquals(userName) && u.compareHash(finalPasswordHash)) {
                session.put("user", u);
                break;
            }
        }
        if (session.get("user") == null) {
            response.setStatusCode(400)
                    .end("User: '" + userName + "' authentication failed");
        } else {
            response.setStatusCode(204)
                    .end("User: '" + userName + "' authentication success");
        }
//        Tools.answerToRequest(response, 204, "User: '" + userName + "' authenticated", thawLogger);
    }



    /*####################################################################*/
    /////////////////// Disconnect from server Handler ///////////////////
    /*####################################################################*/

    // Todo
    static void disconnectFromServerHandle(RoutingContext routingContext, ThawLogger thawLogger) {
        routingContext.clearUser();
        routingContext.response().putHeader("location", "/").setStatusCode(302).end();
    }

    private static void analyzeDisconnectFromServerRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger) {


    }



    /*############################################################*/
    /////////////////// Create Account Handler ///////////////////
    /*############################################################*/

    static void createAccountHandle(RoutingContext routingContext, ThawLogger thawLogger, List<User> authorizedUsers) {

    }


    /*############################################################*/
    /////////////////// Security Check Handler ///////////////////
    /*############################################################*/
    // Todo
    // Check if the user is connected to the server
    static void securityCheckHandle(RoutingContext routingContext, ThawLogger thawLogger, List<User> authorizedUsers) {
        Session session = routingContext.session();
        HttpServerResponse response = routingContext.response();
        User user = session.get("user");
        if (user == null || !authorizedUsers.contains(user)) {
            Tools.answerToRequest(response, 403, "User does not have the access to private api ", thawLogger);
        } else {
            // Poursuis sur celui sur lequel il pointais avant d'arriver la
            routingContext.next();
//            Tools.answerToRequest(response, 200, "All good", thawLogger);
        }
    }



    /*########################################################*/
    /////////////////// Add Channel Handler ///////////////////
    /*########################################################*/

    static void addChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In addChannel request");
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
        } else {
            analyzeAddChannelRequest(session, response, json, thawLogger, channels, users);
        }
    }

    private static void analyzeAddChannelRequest(Session session, HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
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



    /*############################################################*/
    /////////////////// Delete Channel Handler ///////////////////
    /*############################################################*/

    static void deleteChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In deleteChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();

        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeDeleteChannelRequest(response, session, json, thawLogger);
        }
    }

    private static void analyzeDeleteChannelRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger) {
        // TODO : Deconnecter tout les utilisateur du channel avant sa destruction et les
        // remettre sur le channel "default"
//        User user = session.get("user");
    }



    /*################################################################*/
    /////////////////// Connect to Channel Handler ///////////////////
    /*################################################################*/

    static void connectToChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        thawLogger.log(Level.INFO, "In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();
        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeConnecToChannelRequest(response, session, json, thawLogger, channels, users);
        }
    }

    //TODO : refactoriser davantage le code
    private static void analyzeConnecToChannelRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> users) {
        String oldChannelName = json.getString("oldChannelName");
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");

        if (Tools.verifyEmptyOrNull(oldChannelName, channelName, userName)) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> optchannel = Tools.findChannelInList(channels, channelName);

        if (!optchannel.isPresent()) {
            Tools.answerToRequest(response, 400, "Channel :" + channelName + " does not exist", thawLogger);
        } else {
            Channel chan = optchannel.get();
            User user = session.get("user");
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



    /*##########################################################*/
    /////////////////// Send message Handler ///////////////////
    /*##########################################################*/

    // Fonctionne
    // TODO : Traitement des messages en cas de bot et stockage dans la base de donnée
    static void sendMessageHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
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

    // Todo
    private static void analyzeSendMessageRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        long date = System.currentTimeMillis();

        String message = json.getString("message");
        String userName = json.getString("username");
        String channelName = json.getString("channelName");

        if (Tools.verifyEmptyOrNull(message, userName, channelName)) {
            Tools.answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> channelOptional = Tools.findChannelInList(channels, channelName);
        if (!channelOptional.isPresent()) {
            Tools.answerToRequest(response, 400, "Channel: '" + channelName + "' doesn't exist", thawLogger);
            return;
        }
        Channel chan = channelOptional.get();
        User user = session.get("user");

        if (!chan.checkIfUserIsConnected(user)) {
            Tools.answerToRequest(response, 400, "User: '" + user.getName() + "' is not connected to chan", thawLogger);
            return;
        }

        Message mes = MessageFactory.createMessage(user, date, message);

        user.sendMessage(chan, mes);
        // Todo : Analyser le message si un bot est connecté

        // TODO Stocker les information du message dans la base de donnée du channel

        Tools.answerToRequest(response, 200, "Message: " + mes + " sent correctly to channel '" + channelName + '\'', thawLogger);
    }



    /*##########################################################################*/
    /////////////////// Get list message for channel Handler ///////////////////
    /*##########################################################################*/

    static void getListMessageForChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
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



    /*#######################################################################*/
    /////////////////// Get list user for channel Handler ///////////////////
    /*#######################################################################*/

    // Fonctionne
    static void getListUserForChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In getListUserForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            analyzegetListUserForChannelRequest(response, json, thawLogger, channels);
        }
    }

    private static void analyzegetListUserForChannelRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        String channelName = json.getString("channelName");
        String userName = json.getString("userName");
        if (!securityCheckGetListUserForChannel(response, channelName, userName, thawLogger)) {
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



    /*###############################################################*/
    /////////////////// Get list Channels Handler ///////////////////
    /*###############################################################*/

    static void getListChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In getListChannels request");
        HttpServerResponse response = routingContext.response();
        List<String> tmp = channels.stream().map(Channel::getChannelName).collect(Collectors.toList());
        Tools.answerToRequest(response, 200, tmp, thawLogger);
    }


}
