package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This abstract class regroups the commons methods of
 * a HumanUser and Bot.
 */
abstract class AbstractUser implements User {

    final String name;
    final ConcurrentLinkedQueue<Channel> channels;

    AbstractUser(String name) {
        this.name = Objects.requireNonNull(name);
        channels = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractUser)) return false;
        AbstractUser that = (AbstractUser) o;
        return name != null ? name.equals(that.name) : that.name == null && (channels != null ? channels.equals(that.channels) : that.channels == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (channels != null ? channels.hashCode() : 0);
        return result;
    }

    /**
     * This method allow a humanUser to send a message to a specific channel.
     *
     * @param date    a long that represent when the humanUser send the message
     * @param message a String that represent the message to send
     * @param chan    the Channel in which the message will be send
     * @return true if the message has been sent, false otherwise
     */
    public boolean sendMessage(long date, String message, Channel chan) {
        Objects.requireNonNull(chan);
        return chan.addMessageToQueue(this, date, message);
    }

    /**
     * This method allow a humanUser to join a channel that exist
     *
     * @param chan a channel that the humanUser want to join
     * @return true if he has been authorized to join the channel in the other
     * cases return false
     */
    public boolean joinChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return chan.addUserToChan(this);
    }

    /**
     * @param chan the channel to quit
     * @throws IllegalArgumentException if the channel does not exist or if the HumanUser or bot has not joined
     * the channel yet.
     * @return true if the HumanUser or Bot has been removed from the channel, false
     * otherwise
     */
    public boolean quitChannel(Channel chan) {
        Objects.requireNonNull(chan);
        if (this.isUserHuman() || this.isUserBot()) {
            return chan.removeUserFromChan(this);
        }
        throw new IllegalArgumentException("Channel not found or the User haven't been found");
    }


    /**
     * @return true is the user is a bot, false otherwise
     */
    public boolean isUserBot() {
        return false;
    }

    /**
     * @return true is the user is a human, false otherwise
     */
    public boolean isUserHuman() {
        return false;
    }

}
