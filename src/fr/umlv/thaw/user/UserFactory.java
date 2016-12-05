package fr.umlv.thaw.user;

import java.nio.file.Path;

/**
 * Project :Thaw
 * Created by Narex on 30/11/2016.
 */
public class UserFactory {

    public static HumanUser createHumanUser(String nickname, byte[] passwordHash) {
        return new HumanUser(nickname, passwordHash);
    }

    // todo A changer
    public static Bot createBot(String name, Path path, byte[] hashPassword) {
        return new Bot(name, path, hashPassword);
    }

//    public static Bot createBot(String nickname){
//        return new Bot(nickname);
//    }
}
