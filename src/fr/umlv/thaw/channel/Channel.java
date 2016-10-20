package fr.umlv.thaw.channel;

import fr.umlv.thaw.user.Bot;
import fr.umlv.thaw.user.User;

public interface Channel {

    /**
     * This method add a message that has been wrote by a user in a certain date
     *
     * @param nickname the nickname of the User
     * @param date     the date in which the message has been received
     * @param message  the message that must been add
     * @return true if the message has been sent false otherwise
     */
    boolean addMessageToQueue(String nickname, long date, String message);

    /**
     * Delete a message from the channel from the author at a certain instant
     *
     * @param nickname the name of the author
     * @param date     the date in which the message has been sent
     * @return true if the message has been removed, false otherwise
     */
    boolean delMessageFromQueue(String nickname, long date);

    /**
     * Add a user to the channel
     *
     * @param user the user that must been add in the channel
     * @return true if the user has been added, false otherwise
     */
    boolean addUserToChan(User user);

    /**
     * Remove a user from the channel
     *
     * @param user the user to remove from the channel
     * @return true if the user has been removed, false otherwise
     */
    boolean removeUserFromChan(User user);

    /**
     * Add a bot in the channel. That method could be called only by someone who
     * got enough privilege to add a bot in this channel
     *
     * @param bot the bot to add in the channel
     * @return true if the bot has been added, false otherwise
     */
    boolean addBot(Bot bot);

    /**
     * Remove a bot from the channel. That method must be call by someone who
     * got enough privilege to remove a bot from the current channel.
     *
     * @param bot the bot that must been removed from the channel
     * @return true if the bot has been removed, false otherwise
     */
    boolean delBot(Bot bot);
}
