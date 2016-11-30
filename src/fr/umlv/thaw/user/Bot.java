package fr.umlv.thaw.user;

import java.nio.file.Path;
import java.util.Objects;

/**
 * This class represent a bot that could be added in a channel.
 */
public class Bot extends AbstractUser {

    private final Path path;


    Bot(String name, Path path) {
        super(name);
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
    public boolean isUserBot() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bot)) return false;
        if (!super.equals(o)) return false;

        Bot bot = (Bot) o;

        return path != null ? path.equals(bot.path) : bot.path == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
