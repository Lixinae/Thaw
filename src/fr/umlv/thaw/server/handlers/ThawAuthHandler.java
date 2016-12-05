package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.List;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class ThawAuthHandler {
    // Todo
    // Check if the user is connected to the server
    public static void create(RoutingContext routingContext, ThawLogger thawLogger, List<User> authorizedUsers) {
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

}
