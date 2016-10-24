package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;

/**
 * This abstract class regroups the commons methods of
 * a HumanUser and Bot.
 */
abstract class AbstractUser implements User {

    final String name;

    AbstractUser(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return name;
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
        if (this.isUserBot()) {
            return chan.delBot((Bot) this);
        }
        if (this.isUserHuman()) {
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

//    @Override
//    public String toString() {
//        return "AbstractUser{" +
//                "name='" + name + '\'' +
//                '}';
//    }
}
