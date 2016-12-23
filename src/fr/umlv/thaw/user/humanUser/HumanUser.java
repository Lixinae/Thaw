package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.user.User;

/**
 * This interface represent a human in our
 * application.
 */
public interface HumanUser extends User {

    /**
     * @return the user name
     */
    String getName();

    /**
     *
     * @return the hashed password
     */
    String getPasswordHash();

    /**
     * @param passwordHash the password hash
     * @return true if the hash given in argument is the same as the users one
     */
    boolean compareHash(String passwordHash);

}
