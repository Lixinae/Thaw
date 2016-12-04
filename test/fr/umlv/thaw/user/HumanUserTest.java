package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import org.junit.Assert;
import org.junit.Test;


public class HumanUserTest {

    ////////// CONSTRUCTOR ///////////////
    @Test(expected = NullPointerException.class)
    public void constructorNameNull() throws Exception {
        new HumanUser(null);
    }

    @Test
    public void constructorCorrect() throws Exception {
        new HumanUser("truc");
    }

    ////////// IS_USER_HUMAN AND IS_USER_BOT///////////////
    @Test
    public void isUserHumanTrue() throws Exception {
        HumanUser humanUser = new HumanUser("test");
        Assert.assertTrue(humanUser.isUserHuman());
    }

    @Test
    public void isUserBotFalse() throws Exception {
        HumanUser humanUser = new HumanUser("test");
        Assert.assertFalse(humanUser.isUserBot());
    }

    ////////// EQUALS ///////////////
    @Test
    public void equalEqualsSameName() throws Exception {
        User humanUser = new HumanUser("nameTest");
        User humanUser1 = new HumanUser("nameTest");
        Assert.assertEquals(humanUser1, humanUser);
    }

    @Test
    public void notEqualEqualsDifferentName() throws Exception {
        User humanUser = new HumanUser("nameTest");
        User humanUser1 = new HumanUser("nameTestdshufdji");
        Assert.assertNotEquals(humanUser1, humanUser);
    }

    ////////// ADD CHANNEL ///////////////
    @Test
    public void addSingleChannel() throws Exception {
        HumanUser doris = new HumanUser("Doris");
        Channel nemo = ChannelFactory.createChannel(doris, "Nemo");
        doris.addChannel(nemo);
        Assert.assertTrue(doris.channels.contains(nemo));
    }

    @Test
    public void addMultipleChannel() throws Exception {
        HumanUser pinot = new HumanUser("Pinot");
        Channel nemo = ChannelFactory.createChannel(pinot, "Nemo");
        Channel moris = ChannelFactory.createChannel(pinot, "Moris");
        Channel didier = ChannelFactory.createChannel(pinot, "Didier");
        pinot.addChannel(nemo);
        pinot.addChannel(moris);
        pinot.addChannel(didier);
        Assert.assertTrue(pinot.channels.contains(nemo));
        Assert.assertTrue(pinot.channels.contains(moris));
        Assert.assertTrue(pinot.channels.contains(didier));
    }


    ////////// DELETE CHANNEL ///////////////
    @Test
    public void delSingleChannel() throws Exception {
        HumanUser patrice = new HumanUser("Patrice");
        Channel clement = ChannelFactory.createChannel(patrice, "Clement");
        patrice.addChannel(clement);
        Assert.assertTrue(patrice.channels.size() == 1);
        Assert.assertTrue(patrice.deleteChannel(clement));
        Assert.assertTrue(patrice.channels.isEmpty());
    }

    @Test
    public void delMultipleChannel() throws Exception {
        HumanUser lola = new HumanUser("Lola");
        Channel monica = ChannelFactory.createChannel(lola, "Monica");
        Channel juliette = ChannelFactory.createChannel(lola, "Juliette");
        Channel julie = ChannelFactory.createChannel(lola, "Julie");
        lola.addChannel(monica);
        lola.addChannel(juliette);
        lola.addChannel(julie);
        Assert.assertTrue(lola.channels.size() == 3);
        Assert.assertTrue(lola.deleteChannel(monica));
        Assert.assertTrue(lola.channels.size() == 2);
        Assert.assertTrue(lola.deleteChannel(julie));
        Assert.assertTrue(lola.channels.size() == 1);
        Assert.assertTrue(lola.deleteChannel(juliette));
        Assert.assertTrue(lola.channels.isEmpty());
    }

    @Test
    public void delSameChannel() throws Exception {
        HumanUser patrick = new HumanUser("Patrick");
        Channel clementine = ChannelFactory.createChannel(patrick, "Clementine");
        patrick.addChannel(clementine);
        Assert.assertTrue(patrick.deleteChannel(clementine));
        Assert.assertFalse(patrick.deleteChannel(clementine));
    }

}