package fr.umlv.thaw.database;


import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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
     * Set the argument from the prepared request at the index idx at the date "date"
     *
     * @param idx        the index in which the String will be insert into the request
     * @param date       the date
     * @param addToBatch if true then add the prepared request into the batch,
     *                   if false, the request is not finished yet and other arguments will follows
     * @throws SQLException if a database access error occurs or
     *                      this method is called on a closed PreparedStatement
     */
    void setPrepDateValue(int idx, Date date, boolean addToBatch) throws SQLException;

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
     * Close the connections that may been opened by the database
     *
     * @throws SQLException if a database access error occurs
     */
    void close() throws SQLException;
}
