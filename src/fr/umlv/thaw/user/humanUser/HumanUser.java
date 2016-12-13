package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.user.User;

public interface HumanUser extends User {


    String getName();


    /**
     * This method allow a humanUser to join a channel that exist
     *
     * @param chan a channel that the humanUser want to join
     * @return true if he has been authorized to join the channel in the other
     * cases return false
     */
    boolean joinChannel(Channel chan);

    /**
     * @param chan the channel to quit
     * @return true if the humanUser has been remove from the channel, false
     * otherwise
     */
    boolean quitChannel(Channel chan);

    /**
     * This method create a new channel with the current HumanUserImpl as creator
     *
     * @param chan the Channel to create
     * @return true if the Channel has been created false otherwise
     */
    boolean addChannel(Channel chan);

    /**
     * This method will try to delete a Channel that the current HumanUserImpl has created.
     *
     * @param chan the Channel to delete
     * @return true if the Channel has been deleted false otherwise
     */
    boolean deleteChannel(Channel chan);

    /**
     * @param passworHash the password hash
     * @return true if the hash given in argument is the same as the users one
     */
    boolean compareHash(String passworHash);

}
