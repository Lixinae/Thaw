package fr.umlv.thaw.database;


import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public interface Database {




    /**
     * This method insert a new user into the table if he doesn't exist yet.
     * The password will be ecrypt inside the function.
     *
     * @param login    the login of the user
     * @param password the password that's gonna be encrypt
     * @throws NoSuchAlgorithmException if we cannot encrypt with our Algorithm
     * @throws SQLException             if a database access errors occurs
     */
    void createLogin(String login, String password) throws NoSuchAlgorithmException, SQLException;


    /**
     * Create a table that will stock the data from the channel
     *
     * @param channelName the name of the channel
     * @param owner       the owner of the table
     * @throws SQLException if a database access error occurs
     */
    void createChannelTable(String channelName, String owner) throws SQLException;

    /**
     * Create a table that will maintain the list of channels.
     * This method should only be called once at the initialization.
     *
     * @throws SQLException if a database access error occurs
     */
    void createChannelsTable() throws SQLException;

    /**
     * Create a table that will maintain who can watch each channel
     * This method should only be called once at the initialization.
     *
     * @throws SQLException if a database access errors occurs
     */
    void createChanViewerTable() throws SQLException;

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
     * @param date        the date when the message has been sent
     * @param msg         the message to stock
     * @param author      the author of the message
     * @throws SQLException if a database access errors occurs
     */
    void addMessageToChannelTable(String channelName, long date, String msg, String author) throws SQLException;


    /**
     * This method change a message from an author at the moment "date"
     * to a new message.
     *
     * @param channelName the name of the table
     * @param date        the date of the old message
     * @param author      the author of the message
     * @param Oldmsg      the old message to change
     * @param newMsg      the new message that will replace Oldmsg
     * @throws SQLException if a database access errors occurs
     */
    void updateMessageFromChannel(String channelName, long date, String author, String Oldmsg, String newMsg) throws SQLException;


    /**
     * That function return a list of users that are
     * in the database.
     *
     * @return a list that contained the users name
     * @throws SQLException if a database access errors occurs
     */
    List<String> usersList() throws SQLException;


    /**
     * This method retrieve the user from the channel.
     *
     * @param channel the channel name
     * @return an Empty list if no users have been found
     * otherwise this method return a List of user name
     * @throws SQLException if a database access errors occurs
     */
    List<String> retrieveUsersFromChan(String channel) throws SQLException;

    /**
     * This function retrieve the list of messages with every information
     * (with the format DATE(as a long)0X00AUTHOR0X00MESSAGE\n)
     *
     * @param channelName channel in which we want to retrieve the messages
     * @return a String that represents the messages separate with '\n'.
     * @throws SQLException if an error occurs during database access
     */
    String messagesList(String channelName) throws SQLException;

    /**
     * This method return a List of the channels name on the database
     *
     * @return a List that contained every Channels name of the database
     */
    List<String> channelList();

    /**
     * Close the connections that may been opened by the database
     * Must be called once after that we finished with the Database
     *
     * @throws SQLException if a database access error occurs
     */
    void close() throws SQLException;
}
