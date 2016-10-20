package fr.umlv.thaw.message;

import fr.umlv.thaw.user.humanUser.HumanUser;

/**
 * Project :Thaw
 */
public class Message {

    private final HumanUser sender;
    private final long date;
    private final String content;

    public Message(HumanUser sender, long date, String content) {
        this.sender = sender;
        this.date = date;
        this.content = content;
    }

    public HumanUser getSender() {
        return sender;
    }

    public long getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
