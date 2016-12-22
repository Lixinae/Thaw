package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.server.Tools;

import java.util.Objects;

/**
 * This class represent a Human user
 */
public class HumanUserImpl implements HumanUser {

    private final String name;
    private final String passwordHash;

    /**
     * This constructor can throw {@link NullPointerException}
     * if one of the parameter is null.
     *
     * @param nickname     the name of the user
     * @param passwordHash the password that has been hashed
     */
    HumanUserImpl(String nickname, String passwordHash) {
        this.name = Objects.requireNonNull(nickname);
        this.passwordHash = Objects.requireNonNull(passwordHash);
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean joinChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return chan.addUserToChan(this);
    }

    @Override
    public boolean quitChannel(Channel chan) {
        Objects.requireNonNull(chan);
        return chan.removeUserFromChan(this);

    }

    @Override
    public boolean sendMessage(Channel chan, Message message) {
        Objects.requireNonNull(chan);
        Objects.requireNonNull(message);
        return chan.addMessageToQueue(message);
    }

    @Override
    public boolean compareHash(String password) {
        return passwordHash.equals(Tools.toSHA256(password));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumanUserImpl)) return false;

        HumanUserImpl humanUser = (HumanUserImpl) o;

        if (!name.equals(humanUser.name)) return false;
        return passwordHash.equals(humanUser.passwordHash);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + passwordHash.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HumanUser{" +
                "name='" + name + '\'' +
                '}';
    }
}
