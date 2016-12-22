package fr.umlv.thaw.database;

import java.sql.*;
import java.util.Objects;

/**
 * Various tools used to maintain the
 * database
 */
class DatabaseTools {


    /*Execute various request that need update*/

    /**
     * This method perform every actions registered
     * in the current database batch.
     *
     * @param co   the connection opend in the database
     * @param prep the PreparedStatement already used by the request
     * @throws SQLException if a database access errors occurs or if one of the task cannot be perform
     */
    static void executeRegisteredTask(Connection co, PreparedStatement prep) throws SQLException {
        co.setAutoCommit(false);
        prep.executeBatch();
        co.setAutoCommit(true);
    }

    /**
     * This method perform the given query if the query is
     * a query that could update the table (such as INSERT,
     * DELETE or UPDATE), or that return nothing.
     *
     * @param query the query to execute
     * @param state the Statement object opened in the database
     * @throws SQLException if a database access error occurs or
     *                      if we got some problems with the given request
     */
    static void exeUpda(String query, Statement state) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    /**
     * This method must be called when we want to perform
     * static request that must return a value such as SELECT .
     *
     * @param query the query to execute
     * @param state the Statement object opened in the database
     * @return an object called ResultSet that contains the result of the given request
     * @throws SQLException if a database access error occurs or
     *                      if we got some problems with the given request
     */
    static ResultSet executeQuery(String query, Statement state) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }




    /*Prepare insertion*/


    /**
     * This method prepare the insertion into a given table of 2 values
     * with the syntax that the PreparedStatement is waiting for.
     * This method perform nothing else that construct the request.
     *
     * @param tableName the ame of the table in which we ant to insert value
     * @return the SQL request's String corresponding.
     */
    static String prepareInsertTwoValuesIntoTable(String tableName) {
        return "insert into " + Objects.requireNonNull(tableName) + " values (?, ?)";
    }


    /* Insert value*/

    /**
     * This method add the given String into the request stocked in the PreparedStatement
     * given in parameter and can add it to the batch if we ask it.
     *
     * @param idx        the place in which we want to insert the String
     * @param value      the String that must been insert into the PreparedStaement object
     * @param addToBatch is it the last element to add ?
     *                   If true, we add the given PreapredStatement into the batch as a
     *                   SQL Request to perform
     * @param prep       The PreparedStatemet in which we want to add an element
     * @throws SQLException If there is a problem with the Request such as a bad index
     */
    private static void setPrepStringValue(int idx, String value, boolean addToBatch, PreparedStatement prep) throws SQLException {
        Objects.requireNonNull(value);
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setString(idx, value);
        if (addToBatch) {
            prep.addBatch();
        }
    }


    /**
     * Prepare the insertion of 2 String values into the given PreparedStatement and
     * add the request in the batch if no SQLException has happened.
     *
     * @param firstVal  The first String value to add into the PreparedStatement request
     * @param secondVal The second String value to add into the PreparedStatement request
     * @param prep      The PreparedStatement object in which wa have stocked our request
     * @throws SQLException If a problem has happened during the writting of the request
     */
    static void insertTwoValIntoTable(String firstVal, String secondVal, PreparedStatement prep) throws SQLException {
        setPrepStringValue(1, firstVal, false, prep);
        setPrepStringValue(2, secondVal, true, prep);
    }


    /**
     * This request if specially made to prepare the adding of a message into a table
     * with the corrects column.
     *
     * @param date    The date as a long in which the message has been wrote
     * @param message The message to stock
     * @param author  The author of the message
     * @param prep    The PreparedStatement object that stock the request
     * @throws SQLException If an error happened during the writting of the request
     */
    static void insertDateMessageAuthor(long date, String message, String author, PreparedStatement prep) throws SQLException {
        prep.setLong(1, date);
        setPrepStringValue(2, message, false, prep);
        setPrepStringValue(3, author, true, prep);
    }



    /*CREATE TABLE AND UPDATE TABLE*/

    /**
     * This method is mostly used once to create a table that will stock the existing Channels
     * with their respective owner .
     *
     * @param state The Statement object that makes the links between the request and the database
     * @throws SQLException If we got a problem during the creation of the table
     */
    static void createChannelsTable(Statement state) throws SQLException {
        final String query = createChannelsTableRequest();
        exeUpda(query, state);
    }

    /**
     * This method is mostly used once to create a table that will stock the access rights
     * of a Channel for each user.
     *
     * @param state The Statement object that makes the links between the request and the database
     * @throws SQLException If we got a problem during the creation of the table
     */
    static void createChanViewerTable(Statement state) throws SQLException {
        final String query = createChanViewerTableRequest();
        exeUpda(query, state);
    }

    /**
     * This method try to add a new channel into our channels table with the given owner.
     * We can only get every existing channels once in our table. So we shouldn't can't
     * twice this method with the same channelname.
     *
     * @param channelName The name of the Channel that we must add to our channels table
     * @param owner       The owner of the channel (that will be the only one to delete the channel)
     * @param co          The Connection that has been made with the database
     * @throws SQLException If we got a problem when trying to perform the request
     */
    static void updateChannelsTable(String channelName, String owner, Connection co) throws SQLException {
        final String query = prepareInsertTwoValuesIntoTable("channels");
        PreparedStatement prep = co.prepareStatement(query);
        insertTwoValIntoTable(channelName, owner, prep);
        executeRegisteredTask(co, prep);
    }

    /**
     * This method update the chanviewer table by adding a new member to a given
     * channel. After that the user can get access to the channel.
     *
     * @param channelName The name of the channel in which we wants to add the user
     * @param member      The member that we want to give access to the channel
     * @param co          The Connection that has been made with the database
     * @throws SQLException If we got a problem when trying to perform the request
     */
    static void updateChanViewerTable(String channelName, String member, Connection co) throws SQLException {
        final String query = prepareInsertTwoValuesIntoTable("chanviewer");
        PreparedStatement prep = co.prepareStatement(query);
        insertTwoValIntoTable(channelName, member, prep);
        executeRegisteredTask(co, prep);
    }

    /*CREATE AND UPDATE TABLES REQUEST SQL*/

    /**
     * @return The String representation of the SQL request to create the table chanviewer
     */
    private static String createChanViewerTableRequest() {
        return "create table if not exists chanviewer(" +
                "CHANNAME TEXT NOT NULL, " +
                "MEMBER TEXT NOT NULL " +
                ");";
    }

    /**
     * @return @return The String representation of the SQL request to create the table channels
     */
    private static String createChannelsTableRequest() {
        return "create table if not exists channels(" +
                "CHANNAME TEXT NOT NULL, " +
                "OWNER TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(CHANNAME)" +
                ");";
    }

    //Not private because needed in initializeDB()

    /**
     * Because this request is directly used in a public method, we
     * can't makes this method public.
     *
     * @return @return The String representation of the SQL request to create the table users
     */
    static String createUsersTableRequest() {
        return "create table if not exists users(" +
                "LOGIN TEXT NOT NULL, " +
                "PSWD TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(LOGIN)" +
                ");";
    }


    /*CHECK CONSTRAINT*/

    /**
     * This method check if a user can access to a channel by analyzing the table chanviewer.
     *
     * @param channelName The name of the channel to watch
     * @param userName    The name of he user to find
     * @param co          The Connection that has been made with the database
     * @return true if the user can get access the channel, false otherwise
     * @throws SQLException If the request have failed or a database access error has been occurred.
     */
    /*Because we must perform a check twice with the resut given by our PreparedStatement object,
        we must write more than 8 line. The try-with-resources in tmp is mandatory to don't forget
        to close our ResultSet object.
    */
    static boolean canUserViewChannel(String channelName, String userName, Connection co) throws SQLException {
        final String request = "SELECT * FROM chanviewer WHERE MEMBER LIKE ?  AND CHANNAME LIKE ? ;";
        try (PreparedStatement prep = co.prepareStatement(request)) {
            prep.setString(1, userName);
            prep.setString(2, channelName);
            if (prep.execute()) {
                try (ResultSet tmp = prep.getResultSet()) {
                    if (tmp.next()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method perform a verification and tell if the giver username is the owner of the channel or not.
     * If this is the case, then he can :
     * give access to other people to his channel
     * delete the channel
     *
     * @param channelName The name of the channel to watch
     * @param user        The name of the user to find
     * @param co          The Connection that has been made with the database
     * @return true if the user is the owner of the channel, false otherwise
     * @throws SQLException If the request have failed or a database access error has been occurred.
     */
    /*
        Because we must perform a security check and a try-with-resources with our PreparedStement
        object, we can't really write less than 8 lines.
    */
    static boolean userCanControlAccessToChan(String channelName, String user, Connection co) throws SQLException {
        final String request = "SELECT * FROM channels WHERE CHANNAME LIKE ?  AND OWNER LIKE ? ;";
        try (PreparedStatement prep = co.prepareStatement(request)) {
            prep.setString(1, channelName);
            prep.setString(2, user);
            if (prep.execute()) {
                try (ResultSet tmp = prep.getResultSet()) {
                    if (tmp.next()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
