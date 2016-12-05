package fr.umlv.thaw.user;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static fr.umlv.thaw.server.handlers.Tools.hashToSha256;

public class BotTest {

    ////////// CONSTRUCTOR ///////////////
    @Test(expected = NullPointerException.class)
    public void constructorBothNull() throws Exception {
        new Bot(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorNameNull() throws Exception {
        new Bot(null, Paths.get("test"), hashToSha256("nop"));
    }

    @Test(expected = NullPointerException.class)
    public void constructorFileNameNull() throws Exception {
        new Bot("mouha", null, hashToSha256("password"));
    }

    @Test
    public void constructorNoNull() throws Exception {
        new Bot("bidul", Paths.get("test"), hashToSha256("password"));
    }

    ////////// IS_USER_BOT and IS_USER_HUMAN///////////////
    @Test
    public void isUserBotTrue() throws Exception {
        Bot bot = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Assert.assertTrue(bot.isUserBot());
    }

    @Test
    public void isUserHumanFalse() throws Exception {
        Bot bot = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Assert.assertFalse(bot.isUserHuman());
    }

    ////////// EQUALS ///////////////
    @Test
    public void equalEqualsSameNameSameFile() throws Exception {
        Bot bot = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Bot bot2 = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Assert.assertEquals(bot2, bot);
    }

    @Test
    public void notEqualEqualsDifferentNameSameFile() throws Exception {
        Bot bot = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Bot bot2 = new Bot("bvazsf", Paths.get("test"), hashToSha256("password"));
        Assert.assertNotEquals(bot2, bot);
    }

    @Test
    public void notEqualEqualsSameNameDifferentFile() throws Exception {
        Bot bot = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Bot bot2 = new Bot("nameTest", Paths.get("fghh"), hashToSha256("password"));
        Assert.assertNotEquals(bot2, bot);
    }

    @Test
    public void notEqualEqualsDifferentNameDifferentFile() throws Exception {
        Bot bot = new Bot("nameTest", Paths.get("test"), hashToSha256("password"));
        Bot bot2 = new Bot("blirkdsfhu", Paths.get("fghhj"), hashToSha256("password"));
        Assert.assertNotEquals(bot2, bot);
    }

    ////////// HASH CODE ///////////////
//    @Test
//    public void hashCodeSameNameSameFile() throws Exception {
//        Bot bot = new Bot("nameTest","Filetest");
//        Bot bot2 = new Bot("nameTest","Filetest");
//        Assert.assertEquals(bot2.hashCode(),bot.hashCode());
//    }
//
//    @Test
//    public void notEqualHashCodeDifferentNameSameFile() throws Exception {
//        Bot bot = new Bot("nameTest",Paths.get("test"));
//        Bot bot2 = new Bot("bvazsf",Paths.get("test"));
//        Assert.assertNotEquals(bot2.hashCode(),bot.hashCode());
//    }
//
//    @Test
//    public void notEqualHashCodeSameNameDifferentFile() throws Exception {
//        Bot bot = new Bot("nameTest",Paths.get("test"));
//        Bot bot2 = new Bot("nameTest","dsghggjht");
//        Assert.assertNotEquals(bot2.hashCode(),bot.hashCode());
//    }
//
//    @Test
//    public void notEqualHashCodeDifferentNameDifferentFile() throws Exception {
//        Bot bot = new Bot("nameTest",Paths.get("test"));
//        Bot bot2 = new Bot("gfghst","hhght");
//        Assert.assertNotEquals(bot2.hashCode(),bot.hashCode());
//    }

}