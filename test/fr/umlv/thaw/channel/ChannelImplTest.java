package fr.umlv.thaw.channel;

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
        Assert.assertEquals(1, ch.getListMessage().size());
        ch.delMessageFromQueue(gege, 10);
        Assert.assertEquals(0, ch.getListMessage().size());
    }

    @Test
    public void addUserToChan() throws Exception {

    }

    @Test
    public void removeUserFromChan() throws Exception {

    }

    @Test
    public void getChannelName() throws Exception {

    }

    @Test
    public void getListUser() throws Exception {

    }

    @Test
    public void checkIfUserIsConnected() throws Exception {

    }

    @Test
    public void equals() throws Exception {

    }

}