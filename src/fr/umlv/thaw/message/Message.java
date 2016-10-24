package fr.umlv.thaw.message;

import fr.umlv.thaw.user.User;

import java.util.Objects;

/**
 * Project :Thaw
 */
public class Message {

    private final User sender;
    private final long date;
    private final String content;

    public Message(User sender, long date, String content) {
        this.sender = Objects.requireNonNull(sender);
        if (date <= 0) {
            throw new IllegalArgumentException("date must be positive");
        }
        this.date = date;
        this.content = Objects.requireNonNull(content);
    }

    /**
     * This method return the User that have sent the Message.
     *
     * @return The User that have sent the message
     */
    public User getSender() {
        return sender;
    }

    /**
     * This method retrive the date in which the message has been sent.
     * @return the date of the Message as a long.
     */
    public long getDate() {
        return date;
    }

    /**
     * This message is useful to get the content of the message that is a String.
     *
     * @return the content of the Message as a String.
     */
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
