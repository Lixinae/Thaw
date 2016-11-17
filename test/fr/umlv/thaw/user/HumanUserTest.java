package fr.umlv.thaw.user;

import org.junit.Assert;
import org.junit.Test;

/**
 * Project :Thaw
 * Created by Narex on 16/11/2016.
 */
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

    ////////// IS_USER_HUMAN ///////////////
    @Test
    public void isUserHumanTrue() throws Exception {
        HumanUser humanUser = new HumanUser("test");
        Assert.assertTrue(humanUser.isUserHuman());
    }

    @Test
    public void isUserHumanFalse() throws Exception {
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
    public void addChannel() throws Exception {

    }

    ////////// DELETE CHANNEL ///////////////
    @Test
    public void delChannel() throws Exception {

    }

}