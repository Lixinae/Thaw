package fr.umlv.thaw.user.bot;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;


public class BotImplTest {

    ////////// CONSTRUCTOR ///////////////
    @Test(expected = NullPointerException.class)
    public void constructorBothNull() throws Exception {
        new BotImpl(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorNameNull() throws Exception {
        new BotImpl(null, Paths.get("test"));
    }

    @Test(expected = NullPointerException.class)
    public void constructorFileNameNull() throws Exception {
        new BotImpl("mouha", null);
    }

    @Test
    public void constructorNoNull() throws Exception {
        new BotImpl("bidul", Paths.get("test"));
    }


    ////////// EQUALS ///////////////
    @Test
    public void equalEqualsSameNameSameFile() throws Exception {
        BotImpl bot = new BotImpl("nameTest", Paths.get("test"));
        BotImpl bot2 = new BotImpl("nameTest", Paths.get("test"));
        Assert.assertEquals(bot2, bot);
    }

    @Test
    public void notEqualEqualsDifferentNameSameFile() throws Exception {
        BotImpl bot = new BotImpl("nameTest", Paths.get("test"));
        BotImpl bot2 = new BotImpl("bvazsf", Paths.get("test"));
        Assert.assertNotEquals(bot2, bot);
    }

    @Test
    public void notEqualEqualsSameNameDifferentFile() throws Exception {
        BotImpl bot = new BotImpl("nameTest", Paths.get("test"));
        BotImpl bot2 = new BotImpl("nameTest", Paths.get("fghh"));
        Assert.assertNotEquals(bot2, bot);
    }

    @Test
    public void notEqualEqualsDifferentNameDifferentFile() throws Exception {
        BotImpl bot = new BotImpl("nameTest", Paths.get("test"));
        BotImpl bot2 = new BotImpl("blirkdsfhu", Paths.get("fghhj"));
        Assert.assertNotEquals(bot2, bot);
    }


}