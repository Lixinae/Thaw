package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.logger.ThawLogger;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class ThawAuthHandler {
    // Todo
    public static void create(RoutingContext routingContext, ThawLogger thawLogger) {
        Session session = routingContext.session();
        session.get("user");
    }
}
