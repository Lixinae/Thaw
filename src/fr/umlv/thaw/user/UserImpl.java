package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;

/**
 * Project :Thaw
 * Created by Narex on 20/10/2016.
 */
abstract class UserImpl implements User {

    final String name;

    UserImpl(String name) {
        this.name = Objects.requireNonNull(name);
    }

    /**
     * This method allow a humanUser to send a message to a specific channel.
     *
     * @param date    a long that represent when the humanUser send the message
     * @param message a String that represent the message to send
     * @param chan    the Channel in which the message will be send
     * @return true if the message has been sent, false otherwise
     */
    @Override
    public boolean sendMessage(long date, String message, Channel chan) {
        return false;
    }

    /**
     * This method allow a humanUser to join a channel that exist
     *
     * @param chan a channel that the humanUser want to join
     * @return true if he has been authorized to join the channel in the other
     * cases return false
     */
    @Override
    public boolean joinChannel(Channel chan) {
        return false;
    }

    /**
     * @param chan the channel to quit
     * @return true if the humanUser has been remove from the channel, false
     * otherwise
     */
    @Override
    public boolean quitChannel(Channel chan) {
        return false;
    }
}
