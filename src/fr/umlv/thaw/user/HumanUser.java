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

    /**
     * This method create a new channel with the current HumanUser as creator
     *
     * @param chan the Channel to create
     * @return true if the Channel has been created false otherwise
     */
    public boolean addChannel(Channel chan) {
        //TODO Checker si vraiment correct
        Objects.requireNonNull(chan);
        return channels.add(chan);
    }

    /**
     * This method will try to delete a Channel that the current HumanUser has created.
     *
     * @param chan the Channel to delete
     * @return true if the Channel has been deleted false otherwise
     */
    public boolean delChannel(Channel chan) {
        //TODO checker a quel moment virer les utilisateurs qui etaient encore dans la channel
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
