package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.concurrent.ConcurrentLinkedQueue;

public class HumanUser extends UserImpl {

    //    private final String nickname;
    private final ConcurrentLinkedQueue<Channel> channels;

    public HumanUser(String nickname) {
        super(nickname);
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
    public String toString() {
        return "HumanUser{" +
                "nickname='" + name + '\'' +
                ", channels=" + channels +
                '}';
    }
}
