package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.Bot;
import fr.umlv.thaw.user.User;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the implementation of a channel. This class represent the channel itself.
 */
public class ChannelImpl implements Channel {

    private final ConcurrentLinkedQueue<Bot> bots;
    private final String chanName;
    private final User creator;
    //Prendre une LinkedQueue de message car un utilisateur peut envoyer plusieurs messages.
    private final ConcurrentHashMap<User, ConcurrentLinkedQueue<Message>> messagesQueue;


    public ChannelImpl(User creator, String channelName) {
        this.creator = Objects.requireNonNull(creator);
        chanName = Objects.requireNonNull(channelName);
        bots = new ConcurrentLinkedQueue<>();
        messagesQueue = new ConcurrentHashMap<>();
    }



    @Override
    public boolean addMessageToQueue(User user, long date, String message) {
        Objects.requireNonNull(user);
        ConcurrentLinkedQueue lq = messagesQueue.get(user);
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
        if (messagesQueue.containsKey(user)) {
            return messagesQueue.get(user).removeIf(msg -> msg.getDate() == date);
        }
        return false;
    }

    @Override
    public boolean addUserToChan(User user) {
        Objects.requireNonNull(user);
        if (messagesQueue.containsKey(user)) {
            return false;
        }
        messagesQueue.put(user, new ConcurrentLinkedQueue<>());
        return true;
    }

    @Override
    public boolean removeUserFromChan(User user) {
        Objects.requireNonNull(user);
        if (!messagesQueue.containsKey(user)) {
            return false;
        }
        messagesQueue.remove(user);
        return true;
    }

    @Override
    public boolean addBot(Bot bot) {
        Objects.requireNonNull(bot);
        if (!messagesQueue.containsKey(bot)) {
            //The put method will automatically return null because there where no entrance for bot in the
            //messagesQueue before
            return (bots.add(bot) && messagesQueue.put(bot, new ConcurrentLinkedQueue<>()) == null);
        }
        return false;

    }

    @Override
    public boolean delBot(Bot bot) {
        Objects.requireNonNull(bot);
        if (messagesQueue.containsKey(bot)) {
            messagesQueue.remove(bot);
            return bots.remove(bot);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelImpl channel = (ChannelImpl) o;

        if (!bots.equals(channel.bots)) return false;
        if (!chanName.equals(channel.chanName)) return false;
        if (!creator.equals(channel.creator)) return false;
        return messagesQueue.equals(channel.messagesQueue);

    }

    @Override
    public int hashCode() {
        int result = bots.hashCode();
        result = 31 * result + chanName.hashCode();
        result = 31 * result + creator.hashCode();
        result = 31 * result + messagesQueue.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChannelImpl{" +
                "bots=" + bots +
                ", chanName='" + chanName + '\'' +
                ", creator=" + creator +
                ", messagesQueue=" + messagesQueue +
                '}';
    }
}
