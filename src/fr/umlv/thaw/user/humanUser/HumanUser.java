package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.user.User;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

class HumanUser implements User {

    private final String nickname;
    private final ConcurrentLinkedQueue<Channel> channels;

    public HumanUser(String nickname) {
        this.nickname = Objects.requireNonNull(nickname);
        channels = new ConcurrentLinkedQueue<>();
    }

    public boolean addChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean delChannel(Channel chan) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean sendMessage(long date, String message, Channel chan) {
        return false;
    }

    @Override
    public boolean joinChannel(Channel chan) {
        return false;
    }

    @Override
    public boolean quitChannel(Channel chan) {
        return false;
    }

    @Override
    public String toString() {
        return "HumanUser{" +
                "nickname='" + nickname + '\'' +
                ", channels=" + channels +
                '}';
    }
}
