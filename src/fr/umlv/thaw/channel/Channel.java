package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.humanUser.HumanUser;

import java.util.List;
import java.util.Optional;

/**
 * This interface contains every method that is useful to implement a channel.
 */
public interface Channel {

    /**
     * @return the userName where it is called on
     */
    String getName();

    /**
     * This method add a message that has been wrote by a user in a certain date
     *
     * @param message the message that must been add
     * @return true if the message has been sent false otherwise
     */
    boolean addMessageToQueue(Message message);

    /**
     * Delete a message from the channel from the author at a certain instant
     *
     * @param usr  the user that sent the message at the given time
     * @param date the date in which the message has been sent
     * @return true if the message has been removed, false otherwise
     */
    boolean delMessageFromQueue(User usr, long date);

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
     * @return a string containing the channel Name
     */
    String getChannelName();

    /**
     * @return the list of user connected to the channel
     */
    List<User> getListUser();

    /**
     * @param user the user you want you want to check
     * @return true if the user is already connected, false otherwise
     */
    boolean checkIfUserIsConnected(User user);

    /**
     * @return the channel's messages as a List
     */
    List<Message> getListMessage();

    /**
     * @param user The user you want to find
     * @return The user if he exists, optional.empty otherwise
     */
    Optional<User> findUser(User user);

    /**
     * @param user the user you want to check if he is creator
     * @return true if the given user is the creator of the channel
     */
    boolean isUserCreator(HumanUser user);

    /**
     * Moves all users from the current to a new channel
     * @param newChannel The new channel where to move the users on the current channel
     */
    void moveUsersToAnotherChannel(Channel newChannel);

//    /**
//     * Add a bot in the channel. That method could be called only by someone who
//     * got enough privilege to add a bot in this channel
//     *
//     * @param bot the bot to add in the channel
//     * @return true if the bot has been added, false otherwise
//     */
//    boolean addBot(Bot bot);
//
//    /**
//     * Remove a bot from the channel. That method must be call by someone who
//     * got enough privilege to remove a bot from the current channel.
//     *
//     * @param bot the bot that must been removed from the channel
//     * @return true if the bot has been removed, false otherwise
//     */
//    boolean delBot(Bot bot);
}
