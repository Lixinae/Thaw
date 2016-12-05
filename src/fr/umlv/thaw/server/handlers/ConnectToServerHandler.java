package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class ConnectToServerHandler {

    public static void create(RoutingContext routingContext, ThawLogger thawLogger, final List<User> authorizedUsers) {
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
//    private void connectToServer(RoutingContext routingContext, AuthProvider authProvider) {
//        thawLogger.log(Level.INFO, "In connectToServer request");
//
//        HttpServerResponse response = routingContext.response();
//        JsonObject json = routingContext.getBodyAsJson();
//        Session session = routingContext.session();
//        authProvider.authenticate(json,userAsyncResult -> {
//            String userName = json.
//                    session.put("user",)
//        });
//        if (json == null) {
//            answerToRequest(response, 400, "Wrong Json format");
//        } else {
//            analyzeConnecToServerRequest(response,json);
//        }
//    }

    private static void analyzeConnecToServerRequest(Session session, HttpServerResponse response, JsonObject json, ThawLogger thawLogger, List<User> authorizedUsers) {
        String userName = json.getString("userName");
        String password = json.getString("password");
        byte[] passwordHash;
        try {
            passwordHash = Tools.hash(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        byte[] finalPasswordHash = passwordHash;
        for (User u : authorizedUsers) {
            if (u.compareHash(finalPasswordHash)) {
                session.put("user", userName);
                break;
            }
        }
        Tools.answerToRequest(response, 204, "User: '" + userName + " authenticated", thawLogger);
    }
}
