package fr.umlv.thaw.main;

import fr.umlv.thaw.user.Bot;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;

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
    public static void main(String[] args) {
        Map map = new HashMap();

        User a = new Bot("a", "a");
        User b = new HumanUser("blork");
        System.out.println(a);
        System.out.println(b);
        System.out.println(a.isUserHuman());
        System.out.println(a.isUserBot());
        System.out.println(b.isUserHuman());
        System.out.println(b.isUserBot());

    }
}
