package fr.umlv.thaw.message;

import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;
import org.junit.Assert;
import org.junit.Test;

import static fr.umlv.thaw.server.Tools.hashToSha256;

/**
 * Project :Thaw
 * Created by Narex on 16/11/2016.
 */
public class MessageTest {

    /////// CONSTRUCTOR ////////
    @Test(expected = NullPointerException.class)
    public void constructorBothNullDatePositive() throws Exception {
        new Message(null, 10, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorSenderNullDatePositive() throws Exception {
        new Message(null, 10, "truc");
    }

    @Test(expected = NullPointerException.class)
    public void constructorContentNullDatePositive() throws Exception {
        new Message(HumanUserFactory.createHumanUser("truc", hashToSha256("password")), 10, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorDateNegative() throws Exception {
        new Message(HumanUserFactory.createHumanUser("truc", hashToSha256("password")), -10, "truc");
    }

    @Test
    public void constructorAllCorrect() throws Exception {
        new Message(HumanUserFactory.createHumanUser("truc", hashToSha256("password")), 100, "truc");
    }

    /////// GET SENDER ///////
    @Test
    public void getSender() throws Exception {
        HumanUser humanUser = HumanUserFactory.createHumanUser("Blark", hashToSha256("password"));
        Message test = new Message(humanUser, 10, "monMessage");
        Assert.assertEquals(test.getSender(), humanUser);
    }

    /////// GET DATE ///////
    @Test
    public void getDate() throws Exception {
        HumanUser humanUser = HumanUserFactory.createHumanUser("Blark", hashToSha256("password"));
        Message test = new Message(humanUser, 10, "monMessage");
        Assert.assertEquals(test.getDate(), 10);
    }

    /////// GET CONTENT ///////
    @Test
    public void getContent() throws Exception {
        HumanUser humanUser = HumanUserFactory.createHumanUser("Blark", hashToSha256("password"));
        Message test = new Message(humanUser, 10, "monMessage");
        Assert.assertEquals(test.getContent(), "monMessage");
    }

}