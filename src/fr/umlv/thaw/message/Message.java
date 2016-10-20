package fr.umlv.thaw.message;

import fr.umlv.thaw.user.User;

/**
 * Project :Thaw
 */
public class Message {

    private final User sender;
    private final long date;
    private final String content;

    public Message(User sender, long date, String content) {
        this.sender = sender;
        this.date = date;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public long getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", date=" + date +
                ", content='" + content + '\'' +
                '}';
    }
}
