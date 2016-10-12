package fr.umlv.thaw.bot;

/**
 * Project :Thaw
 * Created by Narex on 12/10/2016.
 */
class BotImpl implements Bot {

    private final String name;
    private final String filePropertiesName;

    public BotImpl(String name, String filePropertiesName) {
        this.name = name;
        this.filePropertiesName = filePropertiesName;
    }
}
