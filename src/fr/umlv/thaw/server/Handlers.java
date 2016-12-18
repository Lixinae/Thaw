package fr.umlv.thaw.server;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.database.Database;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
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
    static void connectToServerHandle(RoutingContext routingContext, ThawLogger thawLogger,
                                      List<HumanUser> authorizedHumanUsers,
                                      List<User> connectedUsers,
                                      List<Channel> channels) {
        thawLogger.log(Level.INFO, "In connectToServer request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();

        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format", thawLogger);
        }
        if (session == null) {
            answerToRequest(response, 400, "No session", thawLogger);
        } else {
            analyzeConnecToServerRequest(session, response, json, thawLogger, authorizedHumanUsers, connectedUsers, channels);
        }
    }

    private static void analyzeConnecToServerRequest(Session session, HttpServerResponse response, JsonObject json,
                                                     ThawLogger thawLogger,
                                                     List<HumanUser> authorizedHumanUsers,
                                                     List<User> connectedUsers,
                                                     List<Channel> channels) {
        String userName = json.getString("userName");
        String password = json.getString("password");
        if (verifyEmptyOrNull(userName, password)) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
        }
        boolean containsUser = true;
        for (HumanUser u : authorizedHumanUsers) {
            containsUser = connectedUsers.contains(u);
            if ((u.getName().equals(userName) && u.compareHash(password)) && !containsUser) {
                connectedUsers.add(u);
                session.put("user", u);
                break;
            }
        }
        if (session.get("user") == null || containsUser) {
            answerToRequest(response, 400, "HumanUser: '" + userName + "' authentication failed", thawLogger);
        } else {
            Optional<Channel> optChannel = findChannelInList(channels, "default");
            if (!optChannel.isPresent()) {
                answerToRequest(response, 400, "Channel 'default' does not exist", thawLogger);
                return;
            }
            Channel chan = optChannel.get();
            User u = session.get("user");
            chan.addUserToChan(u);
            answerToRequest(response, 204, "HumanUser: '" + userName + "' authentication success, connected to 'default' channel", thawLogger);
        }
//        answerToRequest(response, 204, "HumanUser: '" + userName + "' authenticated", thawLogger);
    }



    /*####################################################################*/
    /////////////////// Disconnect from server Handler ///////////////////
    /*####################################################################*/

    // Todo
    static void disconnectFromServerHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, List<User> connectedUsers) {
        thawLogger.log(Level.INFO, "In disconnect from server request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
        } else {
            analyzeDisconnectFromServerRequest(routingContext, response, json, thawLogger, channels, connectedUsers);
        }
    }

    private static void analyzeDisconnectFromServerRequest(RoutingContext routingContext, HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels, List<User> connectedUsers) {

        String currentChannel = json.getString("currentChannelName");
        Session session = routingContext.session();

        if (session == null) {
            answerToRequest(response, 400, "No session", thawLogger);
            return;
        }
        if (verifyEmptyOrNull(currentChannel)) {
            answerToRequest(response, 400, "There is no channel defined", thawLogger);
            return;
        }
        Optional<Channel> optChannel = findChannelInList(channels, currentChannel);
        if (!optChannel.isPresent()) {
            answerToRequest(response, 400, "Channel '" + currentChannel + "' does not exist", thawLogger);
            return;
        }
        Channel chan = optChannel.get();
        HumanUser user = session.get("user");
        connectedUsers.remove(user);
        chan.removeUserFromChan(user);
        // Detruit la session courante
        routingContext.session().destroy();
        thawLogger.log(Level.INFO, "Disconnected from server");
        response.putHeader("location", "/").setStatusCode(302).end();
    }



    /*############################################################*/
    /////////////////// Create Account Handler ///////////////////
    /*############################################################*/

    static void createAccountHandle(RoutingContext routingContext, ThawLogger thawLogger, List<HumanUser> authorizedHumanUsers, Database database) {
        thawLogger.log(Level.INFO, "In create account request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
        } else {
            analyzeCreateAccountRequest(response, json, thawLogger, authorizedHumanUsers, database);
        }
    }

    // todo
    private static void analyzeCreateAccountRequest(HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<HumanUser> authorizedHumanUsers, Database database) {
        String userName = json.getString("userName");
        String password = json.getString("password");
        if (verifyEmptyOrNull(userName, password)) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        String hashedPass = Tools.toSHA256(password);
        HumanUser humanUser = HumanUserFactory.createHumanUser(userName, hashedPass);
        if (authorizedHumanUsers.contains(humanUser)) {
            answerToRequest(response, 402, "User '" + userName + "' already exists", thawLogger);
            return;
        }
        try {
            database.createLogin(humanUser);
        } catch (SQLException e) {
            answerToRequest(response, 402, "User '" + userName + "' already exists", thawLogger);
            return;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        authorizedHumanUsers.add(humanUser);
        answerToRequest(response, 200, "Account '" + userName + "' created", thawLogger);
    }

    /*############################################################*/
    /////////////////// Security Check Handler ///////////////////
    /*############################################################*/
    // Todo
    // Check if the user is connected to the server
    static void securityCheckHandle(RoutingContext routingContext, ThawLogger thawLogger, List<HumanUser> authorizedHumanUsers) {
        thawLogger.log(Level.INFO, "In security check handler");
        Session session = routingContext.session();
        HttpServerResponse response = routingContext.response();
        HumanUser humanUser = session.get("user");
        if (humanUser == null || !authorizedHumanUsers.contains(humanUser)) {
            answerToRequest(response, 403, "HumanUser does not have the access to private api ", thawLogger);
        } else {
            // Poursuis sur celui sur lequel il pointais avant d'arriver la
            routingContext.next();
//            answerToRequest(response, 200, "All good", thawLogger);
        }
    }



    /*########################################################*/
    /////////////////// Add Channel Handler ///////////////////
    /*########################################################*/

    static void addChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In addChannel request");
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
        }
        if (session == null) {
            answerToRequest(response, 400, "No session", thawLogger);
        } else {
            analyzeAddChannelRequest(session, response, json, thawLogger, channels);
        }
    }

    private static void analyzeAddChannelRequest(Session session, HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        String newChannelName = json.getString("newChannelName");
        String creatorName = json.getString("creatorName");
        thawLogger.log(Level.INFO, newChannelName + " " + creatorName + " ");
        if (verifyEmptyOrNull(newChannelName, creatorName)) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> optChannel = findChannelInList(channels, newChannelName);
        if (optChannel.isPresent()) {
            answerToRequest(response, 400, "Channel " + newChannelName + " already exists", thawLogger);
        } else {
            HumanUser creator = session.get("user");
            createAndAddChannel(newChannelName, creator, channels);
            answerToRequest(response, 200, "Channel " + newChannelName + " successfully created", thawLogger);
        }
    }

    private static void createAndAddChannel(String newChannelName, HumanUser creator, List<Channel> channels) {
        Channel newChannel = ChannelFactory.createChannel(creator, newChannelName);
        channels.add(newChannel);
//        creator.addChannel(newChannel);
    }



    /*############################################################*/
    /////////////////// Delete Channel Handler ///////////////////
    /*############################################################*/

    // Todo A tester
    static void deleteChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In deleteChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();

        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format", thawLogger);
        }
        if (session == null) {
            answerToRequest(response, 400, "No session", thawLogger);
        } else {
            analyzeDeleteChannelRequest(response, session, json, thawLogger, channels);
        }
    }

    private static void analyzeDeleteChannelRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {

        String channelName = json.getString("channelName");
        if (verifyEmptyOrNull(channelName)) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> optchannel = findChannelInList(channels, channelName);
        if (!optchannel.isPresent()) {
            answerToRequest(response, 400, "Channel '" + channelName + "' does not exist", thawLogger);
            return;
        }
        Channel channel = optchannel.get();
        HumanUser user = session.get("user");
        if (!channel.isUserCreator(user)) {
            answerToRequest(response, 400, "You do not have the right to delete this channel", thawLogger);
            return;
        }
        optchannel = findChannelInList(channels, "default");
        if (!optchannel.isPresent()) {
            answerToRequest(response, 400, "Channel '" + channelName + "' does not exist", thawLogger);
            return;
        }
        Channel defaut = optchannel.get();
        channel.moveUsersToAnotherChannel(defaut);
        channels.remove(channel);
        answerToRequest(response, 200, "Channel '" + channelName + "' successfully deleted", thawLogger);
    }



    /*################################################################*/
    /////////////////// Connect to Channel Handler ///////////////////
    /*################################################################*/

    static void connectToChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In connectToChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format", thawLogger);
        }
        if (session == null) {
            answerToRequest(response, 400, "No session", thawLogger);
        } else {
            analyzeConnecToChannelRequest(response, session, json, thawLogger, channels);
        }
    }

    //TODO : refactoriser davantage le code
    private static void analyzeConnecToChannelRequest(HttpServerResponse response, Session session, JsonObject json, ThawLogger thawLogger, List<Channel> channels) {
        String oldChannelName = json.getString("oldChannelName");
        String channelName = json.getString("channelName");

        if (verifyEmptyOrNull(oldChannelName, channelName)) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> optchannel = findChannelInList(channels, channelName);
        if (!optchannel.isPresent()) {
            answerToRequest(response, 400, "Channel :" + channelName + " does not exist", thawLogger);
        } else {
            Channel chan = optchannel.get();
            HumanUser humanUser = session.get("user");
            // Check if the user is already connected to the given channel
            if (chan.checkIfUserIsConnected(humanUser)) {
                answerToRequest(response, 400, "HumanUser :" + humanUser.getName() + " is already connected", thawLogger);
            } else {
                Optional<Channel> optChannelOld = findChannelInList(channels, oldChannelName);
                if (!optChannelOld.isPresent()) {
                    answerToRequest(response, 400, "OldChannel " + oldChannelName + " does not exist", thawLogger);
                } else {
                    Channel oldChan = optChannelOld.get();
                    if (establishConnection(humanUser, chan, oldChan)) {
                        String answer = "HumanUser :" + humanUser + " successfully quit channel :'" + oldChannelName + '\'' + " and connected to channel :'" + channelName + '\'';
                        answerToRequest(response, 200, answer, thawLogger);
                    } else {
                        String answer = "HumanUser :" + humanUser + " failed to quit or join channel";
                        answerToRequest(response, 400, answer, thawLogger);
                    }
                }
            }
        }
    }

    private static boolean establishConnection(HumanUser humanUser, Channel chan, Channel oldChan) {
        return humanUser.quitChannel(oldChan) && humanUser.joinChannel(chan);
    }



    /*##########################################################*/
    /////////////////// Send message Handler ///////////////////
    /*##########################################################*/

    // Fonctionne
    // TODO : Traitement des messages en cas de bot et stockage dans la base de donnée
    static void sendMessageHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels, Database database) {
        thawLogger.log(Level.INFO, "In sendMessage request");
        JsonObject json = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format", thawLogger);
        }
        if (session == null) {
            answerToRequest(response, 400, "No session", thawLogger);
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

        if (verifyEmptyOrNull(message, userName, channelName)) {
            answerToRequest(response, 400, "Wrong JSON input", thawLogger);
            return;
        }
        Optional<Channel> channelOptional = findChannelInList(channels, channelName);
        if (!channelOptional.isPresent()) {
            answerToRequest(response, 400, "Channel: '" + channelName + "' doesn't exist", thawLogger);
            return;
        }
        Channel chan = channelOptional.get();
        HumanUser humanUser = session.get("user");

        if (!chan.checkIfUserIsConnected(humanUser)) {
            answerToRequest(response, 400, "HumanUser: '" + humanUser.getName() + "' is not connected to chan", thawLogger);
            return;
        }

        Message mes = MessageFactory.createMessage(humanUser, date, message);

        humanUser.sendMessage(chan, mes);
        // Todo : Analyser le message si un bot est connecté

        // TODO Stocker les information du message dans la base de donnée du channel

        answerToRequest(response, 200, "Message: " + mes + " sent correctly to channel '" + channelName + '\'', thawLogger);
    }



    /*##########################################################################*/
    /////////////////// Get list message for channel Handler ///////////////////
    /*##########################################################################*/

    static void getListMessageForChannelHandle(RoutingContext routingContext, ThawLogger thawLogger, List<Channel> channels) {
        thawLogger.log(Level.INFO, "In getListMessageForChannel request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        if (json == null) {
            answerToRequest(response, 400, "Wrong Json format", thawLogger);
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
        Optional<Channel> optChan = findChannelInList(channels, channelName);
        if (optChan.isPresent()) {
            Channel channel = optChan.get();
            List<Message> tmpMess = channel.getListMessage();
            List<Message> returnListMessage = tmpMess.subList(Math.max(tmpMess.size() - numberOfMessageWanted, 0), tmpMess.size());
            answerToRequest(response, 200, returnListMessage, thawLogger);
        } else {
            answerToRequest(response, 400, "Channel: " + channelName + " doesn't exist", thawLogger);
        }
    }

    private static boolean securityCheckGetListMessageForChannel(HttpServerResponse response, String channelName, Integer numberOfMessageWanted, ThawLogger thawLogger) {
        if (verifyEmptyOrNull(channelName)) {
            answerToRequest(response, 400, "No channelName given", thawLogger);
            return false;
        }
        if (numberOfMessageWanted < 1) {
            answerToRequest(response, 400, "Number Of Message must be > 0 !", thawLogger);
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
        if (!securityCheckGetListUserForChannel(response, channelName, thawLogger)) {
            return;
        }
        Optional<Channel> channelOptional = findChannelInList(channels, channelName);
        if (channelOptional.isPresent()) {
            List<String> tmp = channelOptional.get().getListUser().stream().map(User::getName).collect(Collectors.toList());
            answerToRequest(response, 200, tmp, thawLogger);
        } else {
            answerToRequest(response, 400, "Channel:" + channelName + " doesn't exist", thawLogger);
        }
    }

    private static boolean securityCheckGetListUserForChannel(HttpServerResponse response, String channelName, ThawLogger thawLogger) {
        if (verifyEmptyOrNull(channelName)) {
            answerToRequest(response, 400, "No channelName given", thawLogger);
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
        answerToRequest(response, 200, tmp, thawLogger);
    }

    /*######################################################################*/
    /////////////////// Usefull methods for all handlers ///////////////////
    /*######################################################################*/


    private static void answerToRequest(HttpServerResponse response, int code, Object answer, ThawLogger thawLogger) {
        String tmp = Json.encodePrettily(answer);
        if (code >= 200 && code < 300) {
            thawLogger.log(Level.INFO, "code: " + code + "\nanswer: " + tmp);
        } else {
            thawLogger.log(Level.WARNING, "code: " + code + "\nanswer: " + tmp);
        }

        response.setStatusCode(code)
                .putHeader("content-type", "application/json")
                .end(tmp);
    }

    private static Optional<Channel> findChannelInList(List<Channel> channels, String channelName) {
        if (verifyEmptyOrNull(channelName)) {
            return Optional.empty();
        }
        return channels.stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .findFirst();
    }

    private static boolean verifyEmptyOrNull(String... strings) {
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
