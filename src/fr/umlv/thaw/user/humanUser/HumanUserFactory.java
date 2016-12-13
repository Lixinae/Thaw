package fr.umlv.thaw.user.humanUser;

public class HumanUserFactory {
    public static HumanUserImpl createHumanUser(String nickname, String passwordHash) {
        return new HumanUserImpl(nickname, passwordHash);
    }
}
