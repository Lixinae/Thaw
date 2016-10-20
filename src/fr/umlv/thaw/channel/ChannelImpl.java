package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.bot.Bot;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChannelImpl implements Channel {

    private final ConcurrentLinkedQueue<Bot> bots;
    private final String chanName;
    private final User creator;
    /*On prend un String nickname ou on prend l'humanUser tel quel ?*/
//    private final ConcurrentHashMap<HumanUser, ConcurrentMap<Long, String>> messagesQueue;
    private final ConcurrentHashMap<User, Message> messagesQueue;


    public ChannelImpl(User creator, String channelName) {
        this.creator = Objects.requireNonNull(creator);
        chanName = Objects.requireNonNull(channelName);
        bots = new ConcurrentLinkedQueue<>();
        messagesQueue = new ConcurrentHashMap<>();
    }



    @Override
    public boolean addMessageToQueue(String nickname, long date, String message) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delMessageFromQueue(String nickname, long date) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addUserToChan(User user) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeUserFromChan(User user) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addBot(Bot bot) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delBot(Bot bot) {
        // TODO Auto-generated method stub
        return false;
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
