package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class represent a Human user
 */
public class HumanUser extends AbstractUser {

    private final byte[] passworHash;

    HumanUser(String nickname, byte[] passworHash) {
        super(nickname);
        this.passworHash = passworHash;
    }


    public boolean compareHash(final byte[] passworHash) {
        return Arrays.equals(this.passworHash, passworHash);
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
    public boolean deleteChannel(Channel chan) {
        //TODO checker a quel moment virer les utilisateurs qui etaient encore dans la channel
        Objects.requireNonNull(chan);
        return channels.remove(chan);
    }

    @Override
    public String toString() {
        return "HumanUser{" +
                "name='" + name + '\'' +
                '}';
        // Ne pas mettre la liste des channels à l'affichage -> Stack overflow à cause de boucle infini
        // Channels qui affiche le createur , qui affiche ses channels qui affiche le createur qui affiche les channels etc...
    }

    @Override
    public boolean isUserHuman() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof HumanUser && super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
