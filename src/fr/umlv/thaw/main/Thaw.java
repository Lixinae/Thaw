package fr.umlv.thaw.main;

import fr.umlv.thaw.database.Database;
import fr.umlv.thaw.database.DatabaseFactory;
import fr.umlv.thaw.server.Server;
import io.vertx.core.Vertx;

import java.nio.file.Paths;


public class Thaw {

    public static void main(String[] args) throws Exception {


        System.setProperty("vertx.disableFileCaching", "true");
        Database database = DatabaseFactory.createDatabase(Paths.get("./db"), "database");
        database.initializeDB();
        Server server = new Server(true, true, database);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(server);

//        vertx.deployVerticle(server, options);

    }
}
