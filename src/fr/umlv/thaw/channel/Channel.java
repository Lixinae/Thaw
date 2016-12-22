package fr.umlv.thaw.channel;

import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.humanUser.HumanUser;

import java.util.List;

/**
 * This interface contains every method that is useful to implement a channel.
 */
public interface Channel {

    /**
     * @return the creator name of the channel
     */
    String getCreatorName();

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
     * @param user the user you want to check if he is creator
     * @return true if the given user is the creator of the channel
     */
    boolean isUserCreator(HumanUser user);

    /**
     * Moves all users from the current to a new channel
     *
     * @param newChannel The new channel where to move the users on the current channel
     */
    void moveUsersToAnotherChannel(Channel newChannel);
}
