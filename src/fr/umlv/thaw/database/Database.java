package fr.umlv.thaw.database;


import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface Database {

    /**
     * This method allows you to prepare a request for the database
     * with a SQL syntax. For example
     * <p> "insert into people values (?, ?, ?);" </p>
     *
     * @param query the Query to send to the database with SQL syntax
     * @throws SQLException if a database access occurs
     */
    void createPrepState(String query) throws SQLException;

    /**
     * Set the argument from the prepared request at the index idx at the value "value"
     *
     * @param idx        the index in which the String will be insert into the request
     * @param value      the value of the argument
     * @param addToBatch if true then add the prepared request into the batch,
     *                   if false, the request is not finished yet and other arguments will follows
     * @throws SQLException if a database access error occurs or
     *                      this method is called on a closed PreparedStatement
     */
    void setPrepStringValue(int idx, String value, boolean addToBatch) throws SQLException;


    /**
     * Set the argument from the prepared request at the index idx at the value "value"
     *
     * @param idx        the index in which the String will be insert into the request
     * @param value      the value of the argument
     * @param addToBatch if true then add the prepared request into the batch,
     *                   if false, the request is not finished yet and other arguments will follows
     * @throws SQLException if a database access error occurs or
     *                      this method is called on a closed PreparedStatement
     */
    void setPrepLongValue(int idx, Long value, boolean addToBatch) throws SQLException;


    /**
     * This method allow you to send a simple request with no value given in parameter
     * like :
     * <p>select * from people</p>
     *
     * @param query the Query to send into the database with SQL syntax
     * @return a ResultSet that represent the result of the request
     * on the database
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed
     *                      <code>Statement</code>, the given
     *                      SQL statement produces anything
     *                      other than a single
     *                      <code>ResultSet</code> object, the
     *                      method is called on a
     *                      <code>PreparedStatement</code> or
     *                      <code>CallableStatement</code>
     */
    ResultSet executeQuery(String query) throws SQLException;


    /**
     * Execute a Query that would update the database such as INSERT,
     * DELETE, DROP or any DDL requests. We can also provide an SQL request
     * that will return nothing.
     *
     * @param query the Query to send into the database with SQL syntax
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed
     *                      <code>Statement</code>, the given
     *                      SQL statement produces a <code>ResultSet</code>
     *                      object, the method is called on a
     *                      <code>PreparedStatement</code> or
     *                      <code>CallableStatement</code>
     */
    void exeUpda(String query) throws SQLException;


    /**
     * Execute every request that has been registered on th batch
     *
     * @throws SQLException if an access errors occurs on the database
     */
    void executeRegisteredTask() throws SQLException;

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
