package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.humanUser.HumanUser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the implementation of a channel. This class represent the channel itself.
 */
public class ChannelImpl implements Channel {

    private final String channelName;
    private final HumanUser creator;
    private final ConcurrentHashMap<User, ConcurrentLinkedQueue<Message>> messagesQueue;
    private final ConcurrentLinkedQueue<Message> allMessage;


    ChannelImpl(HumanUser creator, String channelName) {
        this.creator = Objects.requireNonNull(creator);
        this.channelName = Objects.requireNonNull(channelName);
        messagesQueue = new ConcurrentHashMap<>();
        allMessage = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String getCreatorName() {
        return creator.getName();
    }

    @Override
    public boolean addMessageToQueue(Message message) {
        Objects.requireNonNull(message);
        User user = message.getSender();
        if (!messagesQueue.containsKey(message.getSender())) {
            addUserToChan(message.getSender());
        }
        ConcurrentLinkedQueue<Message> lq = messagesQueue.get(user);
        if (lq.add(message)) {
            allMessage.add(message);
            messagesQueue.put(user, lq);
            return messagesQueue.get(user).contains(message);
        }
        return false;
    }

    @Override
    public boolean delMessageFromQueue(User user, long date) {
        Objects.requireNonNull(user);
        if (messagesQueue.containsKey(user)) {
            if (messagesQueue.get(user).removeIf(msg -> msg.getDate() == date)) {
                allMessage.removeIf(msg -> msg.getDate() == date);
                return true;
            }
        }
        return false;
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
        List<User> tmp = new ArrayList<>();
        messagesQueue.forEach((k, v) -> tmp.add(k));
        return tmp;
    }

    @Override
    public boolean checkIfUserIsConnected(User user) {
        Objects.requireNonNull(user);
        return messagesQueue.containsKey(user);
    }

    @Override
    public List<Message> getListMessage() {
        List<Message> tmp = new ArrayList<>();
//        messagesQueue.forEach((key, value) -> tmp.addAll(value));
//        allMessage.forEach(tmp::add);
        tmp.addAll(allMessage);
        tmp.sort(Comparator.comparingLong(Message::getDate));
        return Collections.unmodifiableList(tmp);
    }

    @Override
    public Optional<User> findUser(User user) {
        if (messagesQueue.containsKey(user)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean isUserCreator(HumanUser user) {
        return creator.equals(user);
    }

    @Override
    public void moveUsersToAnotherChannel(Channel newChannel) {
        List<User> tmpToRemove = new ArrayList<>();
        messagesQueue.forEach((k, v) -> {
            newChannel.addUserToChan(k);
            tmpToRemove.add(k);
        });
        tmpToRemove.forEach(messagesQueue::remove);
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
        if (!(o instanceof ChannelImpl)) return false;

        ChannelImpl channel = (ChannelImpl) o;

        if (!channelName.equals(channel.channelName)) return false;
        return creator.equals(channel.creator) && messagesQueue.equals(channel.messagesQueue);
    }

    @Override
    public int hashCode() {
        int result = channelName.hashCode();
        result = 31 * result + creator.hashCode();
        result = 31 * result + messagesQueue.hashCode();
        return result;
    }
}
