package fr.umlv.thaw.user.bot;

import java.nio.file.Path;

public class BotFactory {
    public static Bot createBot(String name, Path test) {
        return new BotImpl(name, test);
    }
}
