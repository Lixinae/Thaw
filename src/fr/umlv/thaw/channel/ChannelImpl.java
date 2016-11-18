package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.HumanUser;
import fr.umlv.thaw.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the implementation of a channel. This class represent the channel itself.
 */
public class ChannelImpl implements Channel {

    private final String channelName;
    private final HumanUser creator;
    //Prendre une LinkedQueue de message car un utilisateur peut envoyer plusieurs messages.
    private final ConcurrentHashMap<User, ConcurrentLinkedQueue<Message>> messagesQueue;


    public ChannelImpl(HumanUser creator, String channelName) {
        this.creator = Objects.requireNonNull(creator);
        this.channelName = Objects.requireNonNull(channelName);
        messagesQueue = new ConcurrentHashMap<>();
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
        if (!messagesQueue.containsKey(user)) {
            addUserToChan(user);
        }
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
        if (!checkIfUserIsConnected(user)) {
            messagesQueue.put(user, new ConcurrentLinkedQueue<>());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeUserFromChan(User user) {
        Objects.requireNonNull(user);
        if (checkIfUserIsConnected(user)) {
            messagesQueue.remove(user);
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
        // TODO
        List<User> tmp = new ArrayList<>();
        messagesQueue.forEach((k, v) -> tmp.add(k));
        return tmp;
    }

    /**
     * @param user the user you want you want to check
     * @return true if the user is already connected, false otherwise
     */
    @Override
    public boolean checkIfUserIsConnected(User user) {
        Objects.requireNonNull(user);
        return messagesQueue.containsKey(user);
    }

    @Override
    public List<Message> getListMessage() {
        List<Message> tmp = new ArrayList<>();
        messagesQueue.forEach((key, value) -> tmp.addAll(value));
        return Collections.unmodifiableList(tmp);
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
        if (o == null || getClass() != o.getClass()) return false;

        ChannelImpl channel = (ChannelImpl) o;

        if (!channelName.equals(channel.channelName)) return false;
        if (!creator.equals(channel.creator)) return false;
        return messagesQueue.equals(channel.messagesQueue);

    }

    @Override
    public int hashCode() {
        int result = channelName.hashCode();
        result = 31 * result + creator.hashCode();
        result = 31 * result + messagesQueue.hashCode();
        return result;
    }
}
