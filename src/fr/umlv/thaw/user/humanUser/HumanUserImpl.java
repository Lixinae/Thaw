package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

class HumanUserImpl implements HumanUser {

    private final String nickname;
    private final ConcurrentLinkedQueue<Channel> channels;

    public HumanUserImpl(String nickname) {
        this.nickname = Objects.requireNonNull(nickname);
        channels = new ConcurrentLinkedQueue<>();
    }


    public boolean sendMessage(long date, String message, Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean joinChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }


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