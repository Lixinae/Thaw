package fr.umlv.thaw.database;

import java.sql.*;
import java.util.Objects;

class DatabaseTools {


    /*Execute various request that need update*/

    static void executeRegisteredTask(Connection co, PreparedStatement prep) throws SQLException {
        co.setAutoCommit(false);
        prep.executeBatch();
        co.setAutoCommit(true);
    }

    static void exeUpda(String query, Statement state) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    static ResultSet executeQuery(String query, Statement state) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }




    /*Prepare insertion*/


    // Tu passe toujours des constantes, donc j'ai laisser la fonction, car pas de bug( vu que tu donne que des constantes en parametre )
    static String prepareInsertTwoValuesIntoTable(String tableName) {
        return "insert into " + Objects.requireNonNull(tableName) + " values (?, ?)";
    }


    /* Insert value*/

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


    static void insertTwoValIntoTable(String firstVal, String secondVal, PreparedStatement prep) throws SQLException {
        setPrepStringValue(1, firstVal, false, prep);
        setPrepStringValue(2, secondVal, true, prep);
    }

    static void insertDateMessageAuthor(long date, String message, String author, PreparedStatement prep) throws SQLException {
        prep.setLong(1, date);
        setPrepStringValue(2, message, false, prep);
        setPrepStringValue(3, author, true, prep);
    }



    /*CREATE TABLE AND UPDATE TABLE*/

    static void createChannelsTable(Statement state) throws SQLException {
        final String query = createChannelsTableRequest();
        exeUpda(query, state);
    }

    static void createChanViewerTable(Statement state) throws SQLException {
        final String query = createChanViewerTableRequest();
        exeUpda(query, state);
    }

    static void updateChannelsTable(String channelName, String owner, Connection co) throws SQLException {
        final String query = prepareInsertTwoValuesIntoTable("channels");
        PreparedStatement prep = co.prepareStatement(query);
        insertTwoValIntoTable(channelName, owner, prep);
        executeRegisteredTask(co, prep);
    }

    static void updateChanViewerTable(String channelName, String member, Connection co) throws SQLException {
        final String query = prepareInsertTwoValuesIntoTable("chanviewer");
        PreparedStatement prep = co.prepareStatement(query);
        insertTwoValIntoTable(channelName, member, prep);
        executeRegisteredTask(co, prep);
    }

    /*CREATE AND UPDATE TABLES REQUEST SQL*/

    private static String createChanViewerTableRequest() {
        return "create table if not exists chanviewer(" +
                "CHANNAME TEXT NOT NULL, " +
                "MEMBER TEXT NOT NULL " +
                ");";
    }

    private static String createChannelsTableRequest() {
        return "create table if not exists channels(" +
                "CHANNAME TEXT NOT NULL, " +
                "OWNER TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(CHANNAME)" +
                ");";
    }

    //Not private because needed in initializeDB()
    static String createUsersTableRequest() {
        return "create table if not exists users(" +
                "LOGIN TEXT NOT NULL, " +
                "PSWD TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(LOGIN)" +
                ");";
    }


    /*CHECK CONSTRAINT*/

    static boolean canUserViewChannel(String channelName, String userName, Connection co) throws SQLException {
        final String request = "SELECT * FROM chanviewer WHERE MEMBER LIKE ?  AND CHANNAME LIKE ? ;";
        PreparedStatement prep = co.prepareStatement(request);
        prep.setString(1, userName);
        prep.setString(2, channelName);
        if (prep.execute()) {
            try (ResultSet tmp = prep.getResultSet()) {
                if (tmp.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean userCanControlAccessToChan(String channelName, String user, Connection co) throws SQLException {
        final String request = "SELECT * FROM channels WHERE CHANNAME LIKE ?  AND OWNER LIKE ? ;";
        PreparedStatement prep = co.prepareStatement(request);
        prep.setString(1, channelName);
        prep.setString(2, user);
        if (prep.execute()) {
            try (ResultSet tmp = prep.getResultSet()) {
                if (tmp.next()) {
                    return true;
                }
            }
        }
        return false;
    }


}
