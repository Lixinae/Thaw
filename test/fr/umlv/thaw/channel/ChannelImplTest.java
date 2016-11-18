package fr.umlv.thaw.channel;

import fr.umlv.thaw.user.Bot;
import fr.umlv.thaw.user.HumanUser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project :Thaw
 * Created by Narex on 17/11/2016.
 */
public class ChannelImplTest {
    ///// ADD MESSAGE ////
    @Test
    public void addSingleMessageToQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser gege = new HumanUser("Ggee");
        Assert.assertTrue(ch.addMessageToQueue(gege, 10, "Hey du gland!"));
    }

    @Test
    public void addMultipleMessagesToQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Poney"), "Je sus ici");
        HumanUser dede = new HumanUser("dede");
        Assert.assertTrue(ch.addMessageToQueue(dede, 10, "Hey du gland!"));
        Assert.assertTrue(ch.addMessageToQueue(dede, 10, "Hey du gland!"));
        Assert.assertTrue(ch.addMessageToQueue(dede, 20, "Hey Bapt!"));
        Assert.assertTrue(ch.addMessageToQueue(dede, 50, "Ouais Gaël!"));
        Assert.assertTrue(ch.addMessageToQueue(dede, 70, "Nan rien!"));
        Assert.assertTrue(ch.addMessageToQueue(dede, 81, "Okaay!"));
    }

    ///// DEL MESSAGES /////
    @Test
    public void delMessageFromQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser gege = new HumanUser("Ggee");
        ch.addMessageToQueue(gege, 10, "Hey du gland!");
        Assert.assertEquals(1, ch.getListMessage().size());
        ch.delMessageFromQueue(gege, 10);
        Assert.assertEquals(0, ch.getListMessage().size());
    }


    @Test
    public void delMultipleMessageFromQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser gege = new HumanUser("Ggee");
        ch.addMessageToQueue(gege, 10, "Hey du gland!");
        ch.addMessageToQueue(gege, 30, "Hey du glandos!");
        ch.addMessageToQueue(gege, 50, "Hey bachi!");
        ch.addMessageToQueue(gege, 60, "Hey dsfsdf");
        ch.addMessageToQueue(gege, 70, "Okay");
        ch.addMessageToQueue(gege, 80, "Hey du gland!");
        ch.addMessageToQueue(gege, 90, "Hey du gland!");
        ch.addMessageToQueue(gege, System.currentTimeMillis(), "Hey du gland!");
        ch.addMessageToQueue(gege, System.currentTimeMillis(), "Hey du gland!");
        ch.addMessageToQueue(gege, System.currentTimeMillis(), "Hey du gland!");
        Assert.assertEquals(10, ch.getListMessage().size());
        ch.delMessageFromQueue(gege, 10);
        Assert.assertEquals(9, ch.getListMessage().size());
        ch.delMessageFromQueue(gege, 30);
        ch.delMessageFromQueue(gege, 50);
        ch.delMessageFromQueue(gege, 90);
        Assert.assertEquals(6, ch.getListMessage().size());
    }


    ////ADD USER/////
    @Test
    public void addSingleUserToChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser meme = new HumanUser("Meme");
        Assert.assertTrue(ch.addUserToChan(meme));
    }

    @Test
    public void addMultipleUserToChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser meme = new HumanUser("Meme");
        Bot bot = new Bot("gugu", "propriete");
        HumanUser lze = new HumanUser("lze");
        Assert.assertTrue(ch.addUserToChan(meme));
        Assert.assertTrue(ch.addUserToChan(bot));
        Assert.assertTrue(ch.addUserToChan(lze));
    }

    @Test
    public void addMultipleSameUserToChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser meme = new HumanUser("Meme");
        Assert.assertTrue(ch.addUserToChan(meme));
        Assert.assertFalse(ch.addUserToChan(meme));
    }

    /////Remove User/////
    @Test
    public void removeSingleUserFromChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser meme = new HumanUser("Meme");
        ch.addUserToChan(meme);
        Assert.assertTrue(ch.removeUserFromChan(meme));
    }

    @Test
    public void removeSingleUserMultipleTimeFromChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser meme = new HumanUser("Meme");
        ch.addUserToChan(meme);
        Assert.assertTrue(ch.removeUserFromChan(meme));
        Assert.assertFalse(ch.removeUserFromChan(meme));
    }

    @Test
    public void removeMultipleUserSingleTimeFromChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser riri = new HumanUser("Meme");
        HumanUser fifi = new HumanUser("Gege");
        HumanUser loulou = new HumanUser("Rere");
        ch.addUserToChan(riri);
        ch.addUserToChan(fifi);
        ch.addUserToChan(loulou);
        Assert.assertTrue(ch.removeUserFromChan(riri));
        Assert.assertTrue(ch.removeUserFromChan(fifi));
        Assert.assertTrue(ch.removeUserFromChan(loulou));
    }

    @Test
    public void removeMultipleUserMultipleTimeFromChan() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser riri = new HumanUser("Meme");
        HumanUser fifi = new HumanUser("Gege");
        HumanUser loulou = new HumanUser("Rere");
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
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        Assert.assertEquals("Marie", ch.getChannelName());
    }

    /////GetListUser /////
    @Test
    public void getListUser() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser schlibidi = new HumanUser("schlibidi");
        ch.addUserToChan(schlibidi);
        Assert.assertEquals(1, ch.getListUser().size());
        Assert.assertTrue(ch.getListUser().contains(schlibidi));
    }

    /////GetListUser /////
    @Test
    public void checkIfUserIsConnected() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser schlibidi = new HumanUser("schlibidi");
        ch.addUserToChan(schlibidi);
        Assert.assertTrue(ch.checkIfUserIsConnected(schlibidi));
    }

    @Test
    public void checkIfUserIsNotConnected() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser schlibidi = new HumanUser("schlibidi");
        Assert.assertFalse(ch.checkIfUserIsConnected(schlibidi));
    }

    ///// equals /////
    @Test
    public void equalsSame() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Jackie"), "Et Michelle");
        Assert.assertEquals(ch, ch);
    }


    @Test
    public void equalsTotalDifferent() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Jackie"), "Et Michelle");
        Channel ch2 = new ChannelImpl(new HumanUser("PenPineapple"), "ApplePen");
        Assert.assertNotEquals(ch, ch2);
    }


    @Test
    public void equalsSSameCreatorDifferentName() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Jackie"), "Et Michelle");
        Channel ch2 = new ChannelImpl(new HumanUser("Jackie"), "ApplePen");
        Assert.assertNotEquals(ch, ch2);
    }


    @Test
    public void equalsSSameNameDifferentCreator() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Jackie"), "Et Michelle");
        Channel ch2 = new ChannelImpl(new HumanUser("Didier"), "Et Michelle");
        Assert.assertNotEquals(ch, ch2);
    }



}