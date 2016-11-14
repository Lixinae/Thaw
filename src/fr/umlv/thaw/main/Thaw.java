package fr.umlv.thaw.main;

import fr.umlv.thaw.server.Server;
import io.vertx.core.Vertx;

import java.io.IOException;

/**
 * Project :Thaw
 * Created by Narex on 08/10/2016.
 */
public class Thaw {
    /*
        TODO
     */
    public static void main(String[] args) throws IOException {
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
        Server server = new Server(true);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(server);

//        vertx.deployVerticle(server, options);

//        List<Channel> channels = new ArrayList<>();
//        HumanUser a = new HumanUser("Narex");
//        HumanUser b = new HumanUser("blirk");
//        Channel c = new ChannelImpl(a, "monSuperChan1");
//        Channel c2 = new ChannelImpl(a, "monSuperChan2");
//        channels.add(c);
//        channels.add(c2);
//        System.out.println(channels);
//        System.out.println("############################");
//
//        channels.get(0).addUserToChan(a);
//        a.addChannel(c);
//        channels.get(1).addUserToChan(b);
//        System.out.println(channels);
//        System.out.println("############################");
//
//        Optional<Channel> channelOptional = findChannelInList(channels,"monSuperChan1");
//        if (channelOptional.isPresent()){
//            channelOptional.get().addUserToChan(b);
//        }
//        System.out.println(channels);
//        System.out.println("############################");
//    }
//
//    private static Optional<Channel> findChannelInList(List<Channel> channels,String channelName) {
//        for (Channel c : channels) {
//            if (c.getChannelName().contentEquals(channelName)) {
//                return Optional.of(c);
//            }
//        }
//        return Optional.empty();
//    }
    }
}
