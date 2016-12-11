package fr.umlv.thaw.user.bot;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;

import java.nio.file.Path;
import java.util.Objects;

/**
 * This class represent a bot that could be added in a channel.
 */
public class BotImpl implements Bot {

    private final Path path;
    private final String name;


    BotImpl(String name, Path path) {
        this.name = Objects.requireNonNull(name);
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public String toString() {
        return "Bot{" +
                "name='" + name + '\'' +
                ", path='" + path.getFileName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotImpl)) return false;
        if (!super.equals(o)) return false;

        BotImpl botImpl = (BotImpl) o;

        return path != null ? path.equals(botImpl.path) : botImpl.path == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public boolean sendMessage(Channel chan, Message message) {
        Objects.requireNonNull(chan);
        Objects.requireNonNull(message);
        return chan.addMessageToQueue(message);
    }

    /**
     * @return the user name
     */
    @Override
    public String getName() {
        return name;
    }

}
