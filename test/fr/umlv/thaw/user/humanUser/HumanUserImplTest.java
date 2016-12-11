package fr.umlv.thaw.user.humanUser;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import org.junit.Assert;
import org.junit.Test;

import static fr.umlv.thaw.server.Tools.hashToSha256;


public class HumanUserImplTest {

    ////////// CONSTRUCTOR ///////////////
    @Test(expected = NullPointerException.class)
    public void constructorNameNull() throws Exception {
        new HumanUserImpl(null, null);
    }

    @Test
    public void constructorCorrect() throws Exception {
        new HumanUserImpl("truc", hashToSha256("nop"));
    }

    ////////// EQUALS ///////////////
    @Test
    public void equalEqualsSameName() throws Exception {
        HumanUser humanHumanUser = new HumanUserImpl("nameTest", hashToSha256("password"));
        HumanUser humanHumanUser1 = new HumanUserImpl("nameTest", hashToSha256("password"));
        Assert.assertEquals(humanHumanUser1, humanHumanUser);
    }

    @Test
    public void notEqualEqualsDifferentName() throws Exception {
        HumanUser humanHumanUser = new HumanUserImpl("nameTest", hashToSha256("password"));
        HumanUser humanHumanUser1 = new HumanUserImpl("nameTestdshufdji", hashToSha256("password"));
        Assert.assertNotEquals(humanHumanUser1, humanHumanUser);
    }

    ////////// ADD CHANNEL ///////////////
    @Test
    public void addSingleChannel() throws Exception {
        HumanUserImpl doris = new HumanUserImpl("Doris", hashToSha256("password"));
        Channel nemo = ChannelFactory.createChannel(doris, "Nemo");
        doris.addChannel(nemo);
        Assert.assertTrue(doris.getChannel().contains(nemo));
    }

    @Test
    public void addMultipleChannel() throws Exception {
        HumanUserImpl pinot = new HumanUserImpl("Pinot", hashToSha256("password"));
        Channel nemo = ChannelFactory.createChannel(pinot, "Nemo");
        Channel moris = ChannelFactory.createChannel(pinot, "Moris");
        Channel didier = ChannelFactory.createChannel(pinot, "Didier");
        pinot.addChannel(nemo);
        pinot.addChannel(moris);
        pinot.addChannel(didier);
        Assert.assertTrue(pinot.getChannel().contains(nemo));
        Assert.assertTrue(pinot.getChannel().contains(moris));
        Assert.assertTrue(pinot.getChannel().contains(didier));
    }


    ////////// DELETE CHANNEL ///////////////
    @Test
    public void delSingleChannel() throws Exception {
        HumanUserImpl patrice = new HumanUserImpl("Patrice", hashToSha256("password"));
        Channel clement = ChannelFactory.createChannel(patrice, "Clement");
        patrice.addChannel(clement);
        Assert.assertTrue(patrice.getChannel().size() == 1);
        Assert.assertTrue(patrice.deleteChannel(clement));
        Assert.assertTrue(patrice.getChannel().isEmpty());
    }

    @Test
    public void delMultipleChannel() throws Exception {
        HumanUserImpl lola = new HumanUserImpl("Lola", hashToSha256("password"));
        Channel monica = ChannelFactory.createChannel(lola, "Monica");
        Channel juliette = ChannelFactory.createChannel(lola, "Juliette");
        Channel julie = ChannelFactory.createChannel(lola, "Julie");
        lola.addChannel(monica);
        lola.addChannel(juliette);
        lola.addChannel(julie);
        Assert.assertTrue(lola.getChannel().size() == 3);
        Assert.assertTrue(lola.deleteChannel(monica));
        Assert.assertTrue(lola.getChannel().size() == 2);
        Assert.assertTrue(lola.deleteChannel(julie));
        Assert.assertTrue(lola.getChannel().size() == 1);
        Assert.assertTrue(lola.deleteChannel(juliette));
        Assert.assertTrue(lola.getChannel().isEmpty());
    }

    @Test
    public void delSameChannel() throws Exception {
        HumanUserImpl patrick = new HumanUserImpl("Patrick", hashToSha256("password"));
        Channel clementine = ChannelFactory.createChannel(patrick, "Clementine");
        patrick.addChannel(clementine);
        Assert.assertTrue(patrick.deleteChannel(clementine));
        Assert.assertFalse(patrick.deleteChannel(clementine));
    }

}