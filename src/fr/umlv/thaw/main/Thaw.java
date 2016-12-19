package fr.umlv.thaw.main;

import fr.umlv.thaw.database.Database;
import fr.umlv.thaw.database.DatabaseFactory;
import fr.umlv.thaw.server.Server;
import io.vertx.core.Vertx;

import java.nio.file.Paths;

/**
 * Project :Thaw
 * Created by Narex on 08/10/2016.
 */
public class Thaw {
    /*
        TODO
     */
    public static void main(String[] args) throws Exception {
//        Map map = new HashMap();
//
//        User a = new Bot("a", "a");
//        User b = new HumanUser("blork");
////        System.out.println(a);
////        System.out.println(b);
////        System.out.println(a.isUserHuman());
////        System.out.println(a.isUserBot());
////        System.out.println(b.isUserHuman());
////        System.out.println(b.isUserBot());
//        Configuration test = new Configuration("./botConfig/botConfigurations.txt");
//        test.readConfigurationFromFile();
//        test.printMap();
//        JShell jShell = JShell.create();
//        List<SnippetEvent> l = jShell.eval("int x = 10");
//        l.forEach(System.out::println);

//        ServerSocket socket = new ServerSocket(0);
//        int port = socket.getLocalPort();
//        socket.close();
//        System.out.println(args[0]);
//        DeploymentOptions options = new DeploymentOptions()
//                .setConfig(new JsonObject().put("http.port", port));

        // development option, avoid caching to see changes of
        // static files without having to reload the application,
        // obviously, this line should be commented in production

        System.setProperty("vertx.disableFileCaching", "true");
        Database database = DatabaseFactory.createDatabase(Paths.get("./db"), "database");
        database.initializeDB();
        System.out.println("avant server");
        Server server = new Server(true, true, database);
        System.out.println("apres server");
        System.out.println("avant Vertx.vertx()");
        Vertx vertx = Vertx.vertx();
        System.out.println("apres Vertx.vertx()");
        System.out.println("before deploy");
        vertx.deployVerticle(server);
        System.out.println("after deploy");

//        vertx.deployVerticle(server, options);

    }
}
