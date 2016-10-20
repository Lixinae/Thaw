package fr.umlv.thaw.main;

import fr.umlv.thaw.user.Bot;
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
        System.out.println(a);
    }
}
