package fr.umlv.thaw.user.bot;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.user.User;

/**
 * Project :Thaw
 * Created by Narex on 12/10/2016.
 */
public class Bot implements User {

    private final String name;
    private final String filePropertiesName;

    public Bot(String name, String filePropertiesName) {
        this.name = name;
        this.filePropertiesName = filePropertiesName;
    }


    @Override
    public boolean sendMessage(long date, String message, Channel chan) {
        return false;
    }

    @Override
    public boolean joinChannel(Channel chan) {
        return false;
    }

    @Override
    public boolean quitChannel(Channel chan) {
        return false;
    }

    @Override
    public String toString() {
        return "Bot{" +
                "name='" + name + '\'' +
                ", filePropertiesName='" + filePropertiesName + '\'' +
                '}';
    }
}
