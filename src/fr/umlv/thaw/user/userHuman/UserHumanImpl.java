package fr.umlv.thaw.user.userHuman;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserHumanImpl implements UserHuman {

    private final String nickname;
    private final ConcurrentLinkedQueue<Channel> channels;

    public UserHumanImpl(String nickname) {
        this.nickname = Objects.requireNonNull(nickname);
        channels = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean sendMessage(long date, String message, Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean joinChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean quitChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

}
