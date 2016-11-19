package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;

/**
 * Project :Thaw
 * Created by Narex on 20/10/2016.
 */
public interface User {


    String getName();

    /**
     * This method allow a humanUser to send a message to a specific channel.
     *
     * @param message the message to send
     * @param chan    the Channel in which the message will be send
     * @return true if the message has been sent, false otherwise
     */
    boolean sendMessage(Channel chan, Message message);

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
     * @return true is the user is a bot, false otherwise
     */
    boolean isUserBot();

    /**
     * @return true is the user is a human, false otherwise
     */
    boolean isUserHuman();

}
