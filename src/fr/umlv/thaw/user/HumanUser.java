package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represent a Human user
 */
public class HumanUser extends AbstractUser {

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

    @Override
    public boolean isUserHuman() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HumanUser humanUser = (HumanUser) o;

        return channels.equals(humanUser.channels);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + channels.hashCode();
        return result;
    }
}
