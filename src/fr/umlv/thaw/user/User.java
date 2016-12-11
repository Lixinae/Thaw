package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;

public interface User {

    /**
     * This method allow a HumanUser to send a message to a specific channel.
     *
     * @param message the message to send
     * @param chan    the Channel in which the message will be send
     * @return true if the message has been sent, false otherwise
     */
    boolean sendMessage(Channel chan, Message message);

    /**
     *
     * @return the user name
     */
    String getName();


}
