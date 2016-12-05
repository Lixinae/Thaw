package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.logger.ThawLogger;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.logging.Level;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class ConnectToServerHandler {

    public static void create(RoutingContext routingContext, ThawLogger thawLogger) {
        thawLogger.log(Level.INFO, "In connectToServer request");
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();
        Session session = routingContext.session();

        if (json == null) {
            Tools.answerToRequest(response, 400, "Wrong Json format", thawLogger);
        } else {
            analyzeConnecToServerRequest(session, response, json);
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

    private static void analyzeConnecToServerRequest(Session session, HttpServerResponse response, JsonObject json) {


    }
}
