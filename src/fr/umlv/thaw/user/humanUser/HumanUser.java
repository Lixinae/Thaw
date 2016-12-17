package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.user.User;

public interface HumanUser extends User {

    /**
     * @return the user name
     */
    String getName();

    /**
     *
     * @return the hashed password
     */
    String getPasswordHash();

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
     * @param passworHash the password hash
     * @return true if the hash given in argument is the same as the users one
     */
    boolean compareHash(String passworHash);

}
