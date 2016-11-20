package fr.umlv.thaw.user;

import fr.umlv.thaw.channel.ChannelImpl;
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
        ChannelImpl nemo = new ChannelImpl(doris, "Nemo");
        doris.addChannel(nemo);
        Assert.assertTrue(doris.channels.contains(nemo));
    }

    @Test
    public void addMultipleChannel() throws Exception {
        HumanUser pinot = new HumanUser("Pinot");
        ChannelImpl nemo = new ChannelImpl(pinot, "Nemo");
        ChannelImpl moris = new ChannelImpl(pinot, "Moris");
        ChannelImpl didier = new ChannelImpl(pinot, "Didier");
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
        ChannelImpl clement = new ChannelImpl(patrice, "Clement");
        patrice.addChannel(clement);
        Assert.assertTrue(patrice.channels.size() == 1);
        Assert.assertTrue(patrice.delChannel(clement));
        Assert.assertTrue(patrice.channels.isEmpty());
    }

    @Test
    public void delMultipleChannel() throws Exception {
        HumanUser lola = new HumanUser("Lola");
        ChannelImpl monica = new ChannelImpl(lola, "Monica");
        ChannelImpl juliette = new ChannelImpl(lola, "Juliette");
        ChannelImpl julie = new ChannelImpl(lola, "Julie");
        lola.addChannel(monica);
        lola.addChannel(juliette);
        lola.addChannel(julie);
        Assert.assertTrue(lola.channels.size() == 3);
        Assert.assertTrue(lola.delChannel(monica));
        Assert.assertTrue(lola.channels.size() == 2);
        Assert.assertTrue(lola.delChannel(julie));
        Assert.assertTrue(lola.channels.size() == 1);
        Assert.assertTrue(lola.delChannel(juliette));
        Assert.assertTrue(lola.channels.isEmpty());
    }

    @Test
    public void delSameChannel() throws Exception {
        HumanUser patrick = new HumanUser("Patrick");
        ChannelImpl clementine = new ChannelImpl(patrick, "Clementine");
        patrick.addChannel(clementine);
        Assert.assertTrue(patrick.delChannel(clementine));
        Assert.assertFalse(patrick.delChannel(clementine));
    }

}