package fr.umlv.thaw.database;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.user.humanUser.HumanUser;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public interface Database {

    /**
     * Initialize the database with the necessary tables.
     * Must be launch only once at the creation of the
     * database.
     * Otherwise, an error could occur.
     *
     * @throws SQLException if a database access error has occurred
     */
    void initializeDB() throws SQLException;


    /**
     * Try to add the given user into the database if he isn't in yet.
     *
     * @param humanUser the User to add
     * @throws NoSuchAlgorithmException if we cannot use the SHA256 algorithm
     * @throws SQLException             if a database error occurs
     */
    void createLogin(HumanUser humanUser) throws NoSuchAlgorithmException, SQLException;

    /**
     * Create a channel with the given name and owner, if the
     * table doesn't exist yet
     *
     * @param channelName the name of the channel
     * @param owner       the owner of the channel
     * @throws SQLException if a database error occurs
     */
    void createChannelTable(String channelName, String owner) throws SQLException;

    /**
     * This method allow the autohority to give access to the
     * channel at toAuthorized. It works only if authority as enought
     * right on the channel.
     *
     * @param channel      the channel in which we want to add a user
     * @param toAuthorized the user name that can get access to channel
     * @param authority    the user that grant the access
     * @throws SQLException if a database access errors occurs
     */
    void addUserToChan(String channel, String toAuthorized, String authority) throws SQLException;

    /**
     * This method allow the autohority to remove access to the
     * channel at toAurized. It works only if authority as enought
     * right on the channel.
     * <p>
     * If the authority of the table have the right and wants to kick himself
     * then the channels will be drop to keep the database in a correct state
     *
     * @param channel   the channel in which we want to add a user
     * @param toKick    the user name that will loose access to channel
     * @param authority the user that grant the access
     * @throws SQLException if a database access occurs
     */
    void removeUserAccessToChan(String channel, String toKick, String authority) throws SQLException;

    /**
     * This method allows us to stock a message into a table that could be created
     * if he doesn't exist yet
     *
     * @param channelName the table in which we must insert the message
     * @param msg         the message to stock
     * @throws SQLException if a database access errors occurs
     */
    void addMessageToChannelTable(String channelName, Message msg) throws SQLException;

    /**
     * That function return a list of users that are
     * in the database.
     *
     * @return a list that contained all the HumanUser of the database
     * @throws SQLException if a database access errors occurs
     */
    List<HumanUser> getAllUsersList() throws SQLException;

    /**
     * This method retrieve the user from the channel.
     *
     * @param channel the channel name
     * @return an Empty list if no users have been found
     * otherwise this method return a List of HumanUser
     * @throws SQLException if a database access errors occurs
     */
    List<HumanUser> getUsersListFromChan(String channel) throws SQLException;

    /**
     * This function retrieve the list of messages with every information
     *
     * @param channelName channel in which we want to retrieve the messages
     * @return a String that represents the messages separate with '\n'.
     * @throws SQLException if an error occurs during database access
     */
    List<Message> getMessagesList(String channelName) throws SQLException;

    /**
     * This method return a List of the channels name on the database
     *
     * @return a List that contained every Channels name of the database
     */
    List<Channel> getChannelList();


}
