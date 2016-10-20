package fr.umlv.thaw.message;

import fr.umlv.thaw.user.humanUser.UserHuman;

/**
 * Project :Thaw
 */
public class Message {

    private final UserHuman sender;
    private final long date;
    private final String content;

    public Message(UserHuman sender, long date, String content) {
        this.sender = sender;
        this.date = date;
        this.content = content;
    }

    public UserHuman getSender() {
        return sender;
    }

    public long getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
