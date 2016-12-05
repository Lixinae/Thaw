package fr.umlv.thaw.message;

import fr.umlv.thaw.user.User;
import fr.umlv.thaw.user.UserFactory;
import org.junit.Assert;
import org.junit.Test;

import static fr.umlv.thaw.server.handlers.Tools.hashToSha256;

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
        new Message(UserFactory.createHumanUser("truc", hashToSha256("password")), 10, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorDateNegative() throws Exception {
        new Message(UserFactory.createHumanUser("truc", hashToSha256("password")), -10, "truc");
    }

    @Test
    public void constructorAllCorrect() throws Exception {
        new Message(UserFactory.createHumanUser("truc", hashToSha256("password")), 100, "truc");
    }

    /////// GET SENDER ///////
    @Test
    public void getSender() throws Exception {
        User user = UserFactory.createHumanUser("Blark", hashToSha256("password"));
        Message test = new Message(user, 10, "monMessage");
        Assert.assertEquals(test.getSender(), user);
    }

    /////// GET DATE ///////
    @Test
    public void getDate() throws Exception {
        User user = UserFactory.createHumanUser("Blark", hashToSha256("password"));
        Message test = new Message(user, 10, "monMessage");
        Assert.assertEquals(test.getDate(), 10);
    }

    /////// GET CONTENT ///////
    @Test
    public void getContent() throws Exception {
        User user = UserFactory.createHumanUser("Blark", hashToSha256("password"));
        Message test = new Message(user, 10, "monMessage");
        Assert.assertEquals(test.getContent(), "monMessage");
    }

}