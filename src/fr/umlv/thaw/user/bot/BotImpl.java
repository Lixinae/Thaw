package fr.umlv.thaw.user.bot;

import fr.umlv.thaw.channel.Channel;

/**
 * Project :Thaw
 * Created by Narex on 12/10/2016.
 */
class BotImpl implements Bot {

    private final String name;
    private final String filePropertiesName;

    public BotImpl(String name, String filePropertiesName) {
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
}
