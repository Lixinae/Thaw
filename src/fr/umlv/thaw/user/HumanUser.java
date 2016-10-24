package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HumanUser extends AbstractUser {

    //    private final String nickname;
    private final ConcurrentLinkedQueue<Channel> channels;

    public HumanUser(String nickname) {
        super(nickname);
        channels = new ConcurrentLinkedQueue<>();
    }

    public boolean addChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return channels.add(chan);
    }

    public boolean delChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return channels.remove(chan);
    }

    @Override
    public String toString() {
        return "HumanUser{" +
                "name='" + name + '\'' +
                ", channels=" + channels +
                '}';
    }

    /**
     * @return true is the user is a human, false otherwise
     */
    @Override
    public boolean isUserHuman() {
        return true;
    }
}
