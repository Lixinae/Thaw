package fr.umlv.thaw.channel;

import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.humanUser.HumanUser;
import io.netty.util.internal.ConcurrentSet;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is the implementation of a channel. This class represent the channel itself.
 */
public class ChannelImpl implements Channel {

    private final String channelName;
    private final HumanUser creator;
    private final ConcurrentSet<User> users;
    // We may need a list of message if we are using bots


    ChannelImpl(HumanUser creator, String channelName) {
        this.creator = Objects.requireNonNull(creator);
        this.channelName = Objects.requireNonNull(channelName);
        users = new ConcurrentSet<>();
    }

    @Override
    public String getCreatorName() {
        return creator.getName();
    }

    /*
    * Because we want to do some security check,
    * we must write more than 8 lines to test
    * every possibility.
    * */
    @Override
    public boolean addUserToChan(User user) {
        Objects.requireNonNull(user);
        if (!checkIfUserIsConnected(user)) {
            users.add(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeUserFromChan(User user) {
        Objects.requireNonNull(user);
        if (checkIfUserIsConnected(user)) {
            users.remove(user);
            return true;
        }
        return false;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public List<User> getListUser() {
        return users.stream().collect(Collectors.toList());
    }

    @Override
    public boolean checkIfUserIsConnected(User user) {
        Objects.requireNonNull(user);
        return users.contains(user);
    }


    @Override
    public boolean isUserCreator(HumanUser user) {
        return creator.equals(user);
    }

    @Override
    public boolean areUsersConnected() {
        return !users.isEmpty();
    }


    @Override
    public String toString() {
        return "ChannelImpl{" +
                "channelName='" + channelName + '\'' +
                ", creator=" + creator +
                ", users=" + users +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelImpl)) return false;

        ChannelImpl channel = (ChannelImpl) o;

        if (!channelName.equals(channel.channelName)) return false;
        return creator.equals(channel.creator) && users.equals(channel.users);
    }

    @Override
    public int hashCode() {
        int result = channelName.hashCode();
        result = 31 * result + creator.hashCode();
        result = 31 * result + users.hashCode();
        return result;
    }
}
