package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;

public interface UserHuman {
    /**
     * This method allow a humanUser to send a message to a specific channel.
     *
     * @param date
     *            a long that represent when the humanUser send the message
     * @param message
     *            a String that represent the message to send
     * @param chan
     *            the Channel in which the message will be send
     * @return true if the message has been sent, false otherwise
     */
    boolean sendMessage(long date, String message, Channel chan);

    /**
     * This method allow a humanUser to join a channel that exist
     *
     * @param chan
     *            a channel that the humanUser want to join
     * @return true if he has been authorized to join the channel in the other
     *         cases return false
     */
    boolean joinChannel(Channel chan);

    /**
     *
     * @param chan
     *            the channel to quit
     * @return true if the humanUser has been remove from the channel, false
     *         otherwise
     */
    boolean quitChannel(Channel chan);

    /**
     * Create a channel in which the humanUser will be the master
     *
     * @param chan
     *            the channel to create
     * @return true if the channel has been successfully created, false
     *         otherwise
     */
    boolean addChannel(Channel chan);

    /**
     * This method allow a UserHuman to remove a Channel if he got the permission
     *
     * @param chan
     *            a channel that must been remove
     * @return true if the channel has been removed false otherwise
     */
    boolean delChannel(Channel chan);
}
