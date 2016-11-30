package fr.umlv.thaw.message;

import fr.umlv.thaw.user.User;

/**
 * Project :Thaw
 * Created by Narex on 30/11/2016.
 */
public class MessageFactory {

    public static Message createMessage(User sender, long date, String content) {
        return new Message(sender, date, content);
    }
}
