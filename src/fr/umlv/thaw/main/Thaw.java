package fr.umlv.thaw.main;

import fr.umlv.thaw.configuration.Configuration;
import fr.umlv.thaw.user.Bot;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;
import jdk.jshell.JShell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Project :Thaw
 * Created by Narex on 08/10/2016.
 */
public class Thaw {
    /*
        TODO
     */
    public static void main(String[] args) throws IOException {
        Map map = new HashMap();

        User a = new Bot("a", "a");
        User b = new HumanUser("blork");
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(a.isUserHuman());
//        System.out.println(a.isUserBot());
//        System.out.println(b.isUserHuman());
//        System.out.println(b.isUserBot());
        Configuration test = new Configuration("./botConfig/botConfigurations.txt");
        test.readConfigurationFromFile();
        test.printMap();
        JShell jShell = JShell.create();

    }
}
