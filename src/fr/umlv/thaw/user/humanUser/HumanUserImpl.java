package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represent a Human user
 */
public class HumanUserImpl implements HumanUser {

    private final String name;
    private final ConcurrentLinkedQueue<Channel> channels;
    private final byte[] passwordHash;

    HumanUserImpl(String nickname, byte[] passwordHash) {
        this.name = Objects.requireNonNull(nickname);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        channels = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean addChannel(Channel chan) {
        //TODO Checker si vraiment correct
        Objects.requireNonNull(chan);
        return channels.add(chan);
    }

    public boolean deleteChannel(Channel chan) {
        //TODO checker a quel moment virer les utilisateurs qui etaient encore dans la channel
        Objects.requireNonNull(chan);
        return channels.remove(chan);
    }

    public boolean joinChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return chan.addUserToChan(this);
    }

    public boolean quitChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return chan.removeUserFromChan(this);

    }

    public boolean sendMessage(Channel chan, Message message) {
        Objects.requireNonNull(chan);
        Objects.requireNonNull(message);
        return chan.addMessageToQueue(message);
    }

    public boolean compareHash(final byte[] passworHash) {
        return Arrays.equals(this.passwordHash, passworHash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumanUserImpl)) return false;

        HumanUserImpl humanUser = (HumanUserImpl) o;

        if (!name.equals(humanUser.name)) {
            System.out.println("poirreau");
            return false;
        }
        return Arrays.equals(passwordHash, humanUser.passwordHash);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + channels.hashCode();
        result = 31 * result + Arrays.hashCode(passwordHash);
        return result;
    }

    @Override
    public String toString() {
        return "HumanUser{" +
                "name='" + name + '\'' +
                '}';
        // Ne pas mettre la liste des channels à l'affichage -> Stack overflow à cause de boucle infini
        // Channels qui affiche le createur , qui affiche ses channels qui affiche le createur qui affiche les channels etc...
    }

    ConcurrentLinkedQueue<Channel> getChannel() {
        return channels;
    }
}
