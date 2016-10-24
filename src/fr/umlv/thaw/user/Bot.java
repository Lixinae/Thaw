package fr.umlv.thaw.user;

/**
 * This class represent a bot that could be added in a channel.
 */
public class Bot extends AbstractUser {

    private final String filePropertiesName;

    public Bot(String name, String filePropertiesName) {
        super(name);
        this.filePropertiesName = filePropertiesName;
    }

    @Override
    public String toString() {
        return "Bot{" +
                "name='" + name + '\'' +
                ", filePropertiesName='" + filePropertiesName + '\'' +
                '}';
    }

    @Override
    public boolean isUserBot() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Bot))
            return false;
        if (!super.equals(o))
            return false;

        Bot bot = (Bot) o;

        return filePropertiesName != null ? filePropertiesName.equals(bot.filePropertiesName) : bot.filePropertiesName == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (filePropertiesName != null ? filePropertiesName.hashCode() : 0);
        return result;
    }
}
