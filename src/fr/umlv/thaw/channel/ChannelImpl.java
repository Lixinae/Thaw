package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the implementation of a channel. This class represent the channel itself.
 */
public class ChannelImpl implements Channel {

    //    private final ConcurrentLinkedQueue<Bot> bots;
    private final String channelName;
    private final HumanUser creator;
    //    private final ConcurrentSet<User> users;
    //Prendre une LinkedQueue de message car un utilisateur peut envoyer plusieurs messages.
    private final ConcurrentHashMap<User, ConcurrentLinkedQueue<Message>> messagesQueue;


    public ChannelImpl(HumanUser creator, String channelName) {
        this.creator = Objects.requireNonNull(creator);
        this.channelName = Objects.requireNonNull(channelName);
//        bots = new ConcurrentLinkedQueue<>();
        messagesQueue = new ConcurrentHashMap<>();
//        users = new ConcurrentSet<>();
    }

    /**
     * @return the userName where it is called on
     */
    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public boolean addMessageToQueue(User user, long date, String message) {
        Objects.requireNonNull(user);
        ConcurrentLinkedQueue<Message> lq = messagesQueue.get(user);
        Message msg = new Message(user, date, message);
        if (lq.add(msg)) {
            messagesQueue.put(user, lq);
            return messagesQueue.get(user).contains(msg);
        }
        return false;
    }

    @Override
    public boolean delMessageFromQueue(User user, long date) {
        Objects.requireNonNull(user);
        return messagesQueue.containsKey(user) && messagesQueue.get(user).removeIf(msg -> msg.getDate() == date);
    }

    @Override
    public boolean addUserToChan(User user) {
        Objects.requireNonNull(user);
//        return users.add(user);
        if (messagesQueue.containsKey(user)) {
            return false;
        }
        messagesQueue.put(user, new ConcurrentLinkedQueue<>());
        return true;
    }

    @Override
    public boolean removeUserFromChan(User user) {
        Objects.requireNonNull(user);
//        if (users.contains(user)) {
//            return users.remove(user);
//        }
        if (!messagesQueue.containsKey(user)) {
            return false;
        }
        messagesQueue.remove(user);
        return false;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public List<User> getListUser() {
        // TODO
        List<User> tmp = new ArrayList<>();
        messagesQueue.forEach((k, v) -> tmp.add(k));
        return tmp;
    }

//    @Override
//    public boolean addBot(Bot bot) {
//        Objects.requireNonNull(bot);
//        //The put method will automatically return null because there where no entrance for bot in the
//        //messagesQueue before
//        return !messagesQueue.containsKey(bot) &&
//                (bots.add(bot) &&
//                        messagesQueue.put(bot, new ConcurrentLinkedQueue<>()) == null);
//
//    }
//
//    @Override
//    public boolean delBot(Bot bot) {
//        Objects.requireNonNull(bot);
//        if (messagesQueue.containsKey(bot)) {
//            messagesQueue.remove(bot);
//            return bots.remove(bot);
//        }
//        return false;
//    }


    //    @Override
//    public String toString() {
//        return "ChannelImpl{" +
//                "bots=" + bots +
//                ", channelName='" + channelName + '\'' +
//                ", creator=" + creator +
//                ", messagesQueue=" + messagesQueue +
//                '}';
//    }

    @Override
    public String toString() {
        return "ChannelImpl{" +
                "channelName='" + channelName + '\'' +
                ", creator=" + creator +
                ", messagesQueue=" + messagesQueue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelImpl)) return false;
        ChannelImpl channel = (ChannelImpl) o;
        return channelName != null ? channelName.equals(channel.channelName) : channel.channelName == null && (creator != null ? creator.equals(channel.creator) : channel.creator == null && (messagesQueue != null ? messagesQueue.equals(channel.messagesQueue) : channel.messagesQueue == null));
    }

    @Override
    public int hashCode() {
        int result = channelName != null ? channelName.hashCode() : 0;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (messagesQueue != null ? messagesQueue.hashCode() : 0);
        return result;
    }
}
