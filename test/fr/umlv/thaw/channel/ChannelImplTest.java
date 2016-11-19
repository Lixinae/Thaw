package fr.umlv.thaw.channel;

import fr.umlv.thaw.message.Message;
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
        Message message = new Message(gege, 10, "Hey du gland!");
        Assert.assertTrue(ch.addMessageToQueue(message));
    }

    @Test
    public void addMultipleMessagesToQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Poney"), "Je sus ici");
        HumanUser dede = new HumanUser("dede");
        Message message = new Message(dede, 10, "Hey du gland!");
        Message message1 = new Message(dede, 30, "Hey du glandos!");
        Message message2 = new Message(dede, 50, "Hey bachi!");
        Message message3 = new Message(dede, 60, "Hey dsfsdf");
        Message message4 = new Message(dede, 70, "Okay");
        Message message5 = new Message(dede, 80, "Hey du gland!");
        Assert.assertTrue(ch.addMessageToQueue(message));
        Assert.assertTrue(ch.addMessageToQueue(message1));
        Assert.assertTrue(ch.addMessageToQueue(message2));
        Assert.assertTrue(ch.addMessageToQueue(message3));
        Assert.assertTrue(ch.addMessageToQueue(message4));
        Assert.assertTrue(ch.addMessageToQueue(message5));
    }

    ///// DEL MESSAGES /////
    @Test
    public void delMessageFromQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser gege = new HumanUser("Ggee");
        Message message = new Message(gege, 10, "Hey du gland!");
        ch.addMessageToQueue(message);
        Assert.assertEquals(1, ch.getListMessage().size());
        ch.delMessageFromQueue(gege, 10);
        Assert.assertEquals(0, ch.getListMessage().size());
    }


    @Test
    public void delMultipleMessageFromQueue() throws Exception {
        Channel ch = new ChannelImpl(new HumanUser("Pierre"), "Marie");
        HumanUser gege = new HumanUser("Ggee");
        Message message = new Message(gege, 10, "Hey du gland!");
        Message message1 = new Message(gege, 30, "Hey du glandos!");
        Message message2 = new Message(gege, 50, "Hey bachi!");
        Message message3 = new Message(gege, 60, "Hey dsfsdf");
        Message message4 = new Message(gege, 70, "Okay");
        Message message5 = new Message(gege, 80, "Hey du gland!");
        Message message6 = new Message(gege, 90, "Hey du gland!");
        Message message7 = new Message(gege, System.currentTimeMillis(), "Hey du gland!");
        Message message8 = new Message(gege, System.currentTimeMillis(), "Hey du gland!");
        Message message9 = new Message(gege, System.currentTimeMillis(), "Hey du gland!");
        ch.addMessageToQueue(message);
        ch.addMessageToQueue(message1);
        ch.addMessageToQueue(message2);
        ch.addMessageToQueue(message3);
        ch.addMessageToQueue(message4);
        ch.addMessageToQueue(message5);
        ch.addMessageToQueue(message6);
        ch.addMessageToQueue(message7);
        ch.addMessageToQueue(message8);
        ch.addMessageToQueue(message9);
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