package fr.umlv.thaw.user.humanUser;

/**
 * This is a Static factory for our HumanUserImpl
 */
public class HumanUserFactory {
    public static HumanUserImpl createHumanUser(String nickname, String passwordHash) {
        return new HumanUserImpl(nickname, passwordHash);
    }
}
