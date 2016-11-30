package fr.umlv.thaw.channel;

import fr.umlv.thaw.user.HumanUser;

/**
 * Project :Thaw
 * Created by Narex on 30/11/2016.
 */
public class ChannelFactory {

    public static Channel createChannel(HumanUser creator, String channelName) {
        return new ChannelImpl(creator, channelName);
    }
}
