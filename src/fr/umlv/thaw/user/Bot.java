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
}
