package fr.umlv.thaw.message;

import fr.umlv.thaw.user.User;

/**
 * This class is a static factory for Message
 */
public class MessageFactory {

    public static Message createMessage(User sender, long date, String content) {
        return new Message(sender, date, content);
    }
}
