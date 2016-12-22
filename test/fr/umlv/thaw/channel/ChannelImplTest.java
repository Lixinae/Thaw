package fr.umlv.thaw.channel;

import fr.umlv.thaw.user.bot.Bot;
import fr.umlv.thaw.user.bot.BotFactory;
import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static fr.umlv.thaw.server.Tools.toSHA256;

public class ChannelImplTest {

    ////ADD USER/////
    @Test
    public void addSingleUserToChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser meme = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        Assert.assertTrue(ch.addUserToChan(meme));
    }

    @Test
    public void addMultipleUserToChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser meme = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        Bot botImpl = BotFactory.createBot("gugu", Paths.get("test"));
        HumanUser lze = HumanUserFactory.createHumanUser("lze", toSHA256("password"));
        Assert.assertTrue(ch.addUserToChan(meme));
        Assert.assertTrue(ch.addUserToChan(botImpl));
        Assert.assertTrue(ch.addUserToChan(lze));
    }

    @Test
    public void addMultipleSameUserToChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser meme = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        Assert.assertTrue(ch.addUserToChan(meme));
        Assert.assertFalse(ch.addUserToChan(meme));
    }

    /////Remove HumanUser/////
    @Test
    public void removeSingleUserFromChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser meme = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        ch.addUserToChan(meme);
        Assert.assertTrue(ch.removeUserFromChan(meme));
    }

    @Test
    public void removeSingleUserMultipleTimeFromChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser meme = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        ch.addUserToChan(meme);
        Assert.assertTrue(ch.removeUserFromChan(meme));
        Assert.assertFalse(ch.removeUserFromChan(meme));
    }

    @Test
    public void removeMultipleUserSingleTimeFromChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser riri = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        HumanUser fifi = HumanUserFactory.createHumanUser("Gege", toSHA256("password"));
        HumanUser loulou = HumanUserFactory.createHumanUser("Rere", toSHA256("password"));
        ch.addUserToChan(riri);
        ch.addUserToChan(fifi);
        ch.addUserToChan(loulou);
        Assert.assertTrue(ch.removeUserFromChan(riri));
        Assert.assertTrue(ch.removeUserFromChan(fifi));
        Assert.assertTrue(ch.removeUserFromChan(loulou));
    }

    @Test
    public void removeMultipleUserMultipleTimeFromChan() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser riri = HumanUserFactory.createHumanUser("Meme", toSHA256("password"));
        HumanUser fifi = HumanUserFactory.createHumanUser("Gege", toSHA256("password"));
        HumanUser loulou = HumanUserFactory.createHumanUser("Rere", toSHA256("password"));
        ch.addUserToChan(riri);
        ch.addUserToChan(fifi);
        ch.addUserToChan(loulou);
        Assert.assertTrue(ch.removeUserFromChan(riri));
        Assert.assertTrue(ch.removeUserFromChan(fifi));
        Assert.assertTrue(ch.removeUserFromChan(loulou));
        Assert.assertFalse(ch.removeUserFromChan(riri));
        Assert.assertFalse(ch.removeUserFromChan(fifi));
        Assert.assertFalse(ch.removeUserFromChan(loulou));
    }

    /////GetChannelName /////
    @Test
    public void getChannelName() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        Assert.assertEquals("Marie", ch.getChannelName());
    }

    /////GetListUser /////
    @Test
    public void getListUser() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser schlibidi = HumanUserFactory.createHumanUser("schlibidi", toSHA256("password"));
        ch.addUserToChan(schlibidi);
        Assert.assertEquals(1, ch.getListUser().size());
        Assert.assertTrue(ch.getListUser().contains(schlibidi));
    }

    /////GetListUser /////
    @Test
    public void checkIfUserIsConnected() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser schlibidi = HumanUserFactory.createHumanUser("schlibidi", toSHA256("password"));
        ch.addUserToChan(schlibidi);
        Assert.assertTrue(ch.checkIfUserIsConnected(schlibidi));
    }

    @Test
    public void checkIfUserIsNotConnected() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Pierre", toSHA256("password")), "Marie");
        HumanUser schlibidi = HumanUserFactory.createHumanUser("schlibidi", toSHA256("password"));
        Assert.assertFalse(ch.checkIfUserIsConnected(schlibidi));
    }

    ///// equals /////
    @Test
    public void equalsSame() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Jackie", toSHA256("password")), "Et Michelle");
        Assert.assertEquals(ch, ch);
    }


    @Test
    public void equalsTotalDifferent() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Jackie", toSHA256("password")), "Et Michelle");
        Channel ch2 = new ChannelImpl(HumanUserFactory.createHumanUser("PenPineapple", toSHA256("password")), "ApplePen");
        Assert.assertNotEquals(ch, ch2);
    }


    @Test
    public void equalsSSameCreatorDifferentName() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Jackie", toSHA256("password")), "Et Michelle");
        Channel ch2 = new ChannelImpl(HumanUserFactory.createHumanUser("Jackie", toSHA256("password")), "ApplePen");
        Assert.assertNotEquals(ch, ch2);
    }


    @Test
    public void equalsSSameNameDifferentCreator() throws Exception {
        Channel ch = new ChannelImpl(HumanUserFactory.createHumanUser("Jackie", toSHA256("password")), "Et Michelle");
        Channel ch2 = new ChannelImpl(HumanUserFactory.createHumanUser("Didier", toSHA256("password")), "Et Michelle");
        Assert.assertNotEquals(ch, ch2);
    }



}