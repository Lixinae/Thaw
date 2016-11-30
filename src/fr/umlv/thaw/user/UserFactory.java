package fr.umlv.thaw.user;

import java.nio.file.Path;

/**
 * Project :Thaw
 * Created by Narex on 30/11/2016.
 */
public class UserFactory {

    public static HumanUser createHumanUser(String nickname) {
        return new HumanUser(nickname);
    }

    // todo A changer
    public static Bot createBot(String name, Path path) {
        return new Bot(name, path);
    }

//    public static Bot createBot(String nickname){
//        return new Bot(nickname);
//    }
}
