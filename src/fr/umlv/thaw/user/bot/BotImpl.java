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
    public boolean sendMessage(Channel chan, Message message) {
        Objects.requireNonNull(chan);
        Objects.requireNonNull(message);
        return chan.addMessageToQueue(message);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotImpl)) return false;

        BotImpl bot = (BotImpl) o;

        if (path != null ? !path.equals(bot.path) : bot.path != null) return false;
        return name != null ? name.equals(bot.name) : bot.name == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
