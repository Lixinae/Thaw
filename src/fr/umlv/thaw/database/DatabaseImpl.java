package fr.umlv.thaw.database;


import fr.umlv.thaw.server.Tools;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class represent an implementation of a Database
 * with the SQLITE driver
 */
public class DatabaseImpl implements Database {

    // private final Class sqlite = Class.forName("org.sqlite.JDBC");//to load sqlite.jar
    private final String forGetConnection;
    private final Connection co;
    private final Statement state;
    private PreparedStatement prep;


    /**
     * Construct a representation of our database.
     * The connection is already managed don't forget to close the Database
     *
     * @param pathToDB the path in which the database will be loaded / created
     * @param dbName   the file name of the database without the .db extension
     * @throws ClassNotFoundException if we cannot find the SQLITE library
     * @throws SQLException           if an error occurs during the creation of the database
     */
    public DatabaseImpl(Path pathToDB, String dbName) throws ClassNotFoundException, SQLException {
        Objects.requireNonNull(pathToDB);
        Objects.requireNonNull(dbName);
        Class.forName("org.sqlite.JDBC");
        forGetConnection = "jdbc:sqlite:" + pathToDB + FileSystems.getDefault().getSeparator() + dbName + ".db";
        co = createConnection();
        state = createStatement();
    }


    /*CONSTRUCTOR'S TOOLS*/

    //TODO Ne pas oublier de virer le main une fois que l'on aura fait toutes
    // les methodes necessaire
    public static void main(String[] args) throws Exception {
        String sep = FileSystems.getDefault().getSeparator();
        DatabaseImpl myDB;
        long dte = System.currentTimeMillis();
        try {
            myDB = new DatabaseImpl(Paths.get(".." + sep + "db"), "mafia");//Creation base du fichier mafia.db
        } catch (SQLException sql) {
            System.err.println("can't open the db ");
            return;
        }


        String chan1 = "Chan1";
        //test de createLogin
        try {
            myDB.createChannelsTable();
            myDB.createChanViewerTable();
        } catch (SQLException sql) {
        }
        try {
            myDB.createLogin("George", "12345@A");
            myDB.createLogin("TotoLeBus", "TotoLeBus");
        } catch (SQLException sql) {
            //ne rien faire car pas envie de planter sur une erreur
        }
        //La table chan1 étant supprimer à la fin, on peut la re créer sans risquer une erreur
        myDB.createChannelTable(chan1, "George");

        System.out.println("Nombre de channel : " + myDB.channelList().size());
        myDB.addMessageToChannelTable(chan1, System.currentTimeMillis(), "Bonjour mon message", "George");
        for (String chan : myDB.channelList()) {
            System.out.println("Channel : " + chan);
        }
        System.out.println("Messages dans Chan1 : ");
        System.out.println(myDB.messagesList(chan1));


        myDB.addMessageToChannelTable(chan1, System.currentTimeMillis(), "J'i bien h@ck la secu >: )", "TotoLeBus");


        myDB.addUserToChan("Chan1", "TotoLeBus", "George");
        myDB.addMessageToChannelTable("Chan1", dte, "Avec les droits ça fonctionne mieux", "TotoLeBus");


        System.out.println("Message dans Chan1 deuxième : ");
        System.out.println(myDB.messagesList("Chan1"));


        myDB.updateMessageFromChannel(chan1, dte, "TotoLeBus", "Avec les droits ça fonctionne mieux", "Ah je me suis planté et je vais me perdre :/");

        System.out.println("Apres chgmt de message de TotoLeBus : ");
        System.out.println(myDB.messagesList("Chan1"));


        System.out.println("Avant remove George de Chan1");


        myDB.removeUserAccessToChan(chan1, "George", "George");

        System.out.println("George has been removed from Chan1");
        List<String> chans = myDB.channelList();
        System.out.println("Nombre de channel present après suppresion de l'auteur : " + chans.size());

        System.out.println("Liste d'utilisateur : ");
        myDB.usersList().forEach(System.out::println);


        ResultSet rs = myDB.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
        System.out.println("Liste des tables présentent : ");
        while (rs.next()) {
            System.out.println("name : " + rs.getString(1));
        }


        //Ne pas oublier ensuite de fermer notre bdd et le ResultSet precedemment ouvert.
        //toujours fermer la bdd en dernier sous peine d'erreur

        rs.close();
        myDB.close();
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(forGetConnection);
    }

    private Statement createStatement() throws SQLException {
        Objects.requireNonNull(co);
        return co.createStatement();
    }

    /*
    * Public's method
    * */


    @Override
    public void createLogin(String login, String password) throws NoSuchAlgorithmException, SQLException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        String cryptPass = Tools.toSHA256(password);
        exeUpda(createUsersTableRequest());
        createPrepState(prepareInsertTwoValuesIntoTable("users"));
        insertTwoValIntoTable(login, cryptPass);
        executeRegisteredTask();
    }

    //TODO Garder le try-catch ou le virer ??? Je ne sais pas car je dois garder un comportement qui m'empeche de
    // de rajouter lignes inutiles dans table "channels" et "chanviewer"
    @Override
    public void createChannelTable(String channelName, String owner) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(owner);
        System.out.println("Creation de la table : " + channelName);
        try {
            exeUpda(createChannelTableRequest(channelName));
        } catch (SQLException sql) {
            System.err.println("Table " + channelName + " already exist");
            return;
        }
        updateChannelsTable(channelName, owner);
        updateChanViewerTable(channelName, owner);
    }

    @Override
    public void createChannelsTable() throws SQLException {
        exeUpda(createChannelsTableRequest());
    }

    @Override
    public void createChanViewerTable() throws SQLException {
        exeUpda(createChanViewerTableRequest());
    }

    @Override
    public void addUserToChan(String channel, String toAuthorized, String authority) throws SQLException {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(toAuthorized);
        Objects.requireNonNull(authority);
        if (userCanControlAccessToChan(channel, authority) && !canUserViewChannel(channel, toAuthorized)) {
            updateChanViewerTable(channel, toAuthorized);
        }
    }

    @Override
    public void removeUserAccessToChan(String channel, String toKick, String authority) throws SQLException {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(toKick);
        Objects.requireNonNull(authority);
        if (userCanControlAccessToChan(channel, authority) && !toKick.equals(authority)) {
            removeUserFromChanViewer(channel, toKick);
        } else if (userCanControlAccessToChan(channel, authority) && toKick.equals(authority)) {
            List<String> toEject = retrieveUsersFromChan(channel);
            for (String user : toEject) {
                createPrepState(removeUserFromChanViewerRequest(channel, user));
                prepExecuteUpdate();
            }
            createPrepState(removeChannelFromChannelsRequest(channel, toKick));
            prepExecuteUpdate();
            createPrepState(removeChannel(channel));
            prepExecuteUpdate();
        }
    }

    @Override
    public void addMessageToChannelTable(String channelName, long date, String msg, String author) throws SQLException {
        Objects.requireNonNull(channelName);
        recquirePositive(date);
        Objects.requireNonNull(msg);
        Objects.requireNonNull(author);
        if (canUserViewChannel(channelName, author)) {
            createPrepState(prepareInsertThreeValuesIntoTable(channelName));
            insertDateMessageAuthor(date, msg, author);
            executeRegisteredTask();
        }
    }

    @Override
    public void updateMessageFromChannel(String channelName, long date, String author, String Oldmsg, String newMsg) throws SQLException {
        Objects.requireNonNull(channelName);
        recquirePositive(date);
        Objects.requireNonNull(author);
        Objects.requireNonNull(Oldmsg);
        Objects.requireNonNull(newMsg);
        if (canUserViewChannel(channelName, author)) {
            exeUpda(updateChannelMessageReq(channelName, date, author, Oldmsg, newMsg));
            executeRegisteredTask();
        }
    }

    @Override
    public List<String> usersList() throws SQLException {
        ResultSet rs = executeQuery("select LOGIN from users");
        List<String> userList = new ArrayList<>();
        while (rs.next()) {
            userList.add(rs.getString(1));
        }
        rs.close();
        if (userList.isEmpty()) {
            return Collections.emptyList();
        }
        return userList;
    }


    @Override
    public List<String> retrieveUsersFromChan(String channel) throws SQLException {
        ResultSet rs = executeQuery(retriveUserFromChannelsRequest(Objects.requireNonNull(channel)));
        List<String> users = new ArrayList<>();
        while (rs.next()) {
            users.add(rs.getString(1));
        }
        rs.close();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    @Override
    public String messagesList(String channelName) throws SQLException {
        ResultSet rs = executeQuery("select * from " + channelName);
        StringBuilder messagesList = new StringBuilder();
        while (rs.next()) {
            messagesList.append(rs.getLong("DATE"));
            messagesList.append("0X00");
            messagesList.append(rs.getString("AUTHOR"));
            messagesList.append("0X00");
            messagesList.append(rs.getString("MESSAGE"));
            messagesList.append("\n");
        }
        rs.close();
        return messagesList.toString();
    }

    @Override
    public List<String> channelList() {
        ResultSet rs;
        try {
            rs = executeQuery("SELECT CHANNAME FROM CHANNELS;");
        } catch (SQLException sql) {
            //no result have been found we must return an empty list
            return Collections.emptyList();
        }
        List<String> channels = new ArrayList<>();
        try {
            while (rs.next()) {
                String name = rs.getString(1);
                channels.add(name);
            }
            rs.close();
        } catch (SQLException sql) {
            throw new AssertionError("A database error has been occurred");
        }
        if (channels.isEmpty()) {
            return Collections.emptyList();
        }
        return channels;
    }

    @Override
    public void close() throws SQLException {
        co.close();
    }

    /*PRIVATE METHODS*/

    private void exeBatch() throws SQLException {
        prep.executeBatch();
    }

    private void executeRegisteredTask() throws SQLException {
        setAutoCommit(false);
        exeBatch();
        setAutoCommit(true);
    }

    //The difference between this method and exeUpda is that
    //this method can perfom delete operation on Database
    private void prepExecuteUpdate() throws SQLException {
        prep.executeUpdate();
    }


    private void setAutoCommit(boolean b) throws SQLException {
        co.setAutoCommit(b);
    }


    private void createPrepState(String query) throws SQLException {
        Objects.requireNonNull(query);
        prep = co.prepareStatement(query);
    }


    private void setPrepStringValue(int idx, String value, boolean addToBatch) throws SQLException {
        Objects.requireNonNull(value);
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setString(idx, value);
        if (addToBatch) {
            prep.addBatch();
        }
    }

    private void setPrepLongValue(int idx, Long value, boolean addToBatch) throws SQLException {
        Objects.requireNonNull(value);
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setLong(idx, value);
        if (addToBatch) {
            prep.addBatch();
        }
    }


    private ResultSet executeQuery(String query) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }


    private void exeUpda(String query) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    /* CREATION AND UPDATE REQUEST WITH SQL SYNTAX */

    private String removeChannel(String channel) {
        return "DROP TABLE IF EXISTS " + channel + " ;";
    }


    private String removeChannelFromChannelsRequest(String channel, String toKick) {
        return "DELETE FROM CHANNELS WHERE "
                + "CHANNAME LIKE '" + channel + "' "
                + " AND OWNER LIKE '" + toKick + "';";
    }


    private String removeUserFromChanViewerRequest(String channel, String toKick) {
        return "DELETE FROM CHANVIEWER WHERE "
                + "CHANNAME LIKE '" + channel + "' "
                + " AND MEMBER LIKE '" + toKick + "';";
    }


    private String retriveUserFromChannelsRequest(String channelName) {
        return "SELECT MEMBER FROM CHANVIEWER WHERE "
                + "CHANNAME LIKE '" + channelName + "';";
    }

    private String createUsersTableRequest() {
        return "create table if not exists users(" +
                "LOGIN TEXT NOT NULL, " +
                "PSWD TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(LOGIN)" +
                ");";
    }

    private String createChannelsTableRequest() {
        return "create table if not exists channels(" +
                "CHANNAME TEXT NOT NULL, " +
                "OWNER TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(CHANNAME)" +
                ");";
    }

    private String createChanViewerTableRequest() {
        return "create table if not exists chanviewer(" +
                "CHANNAME TEXT NOT NULL, " +
                "MEMBER TEXT NOT NULL " +
                ");";
    }


    private String createChannelTableRequest(String channelname) {
        return String.format("create table if not exists %s(DATE INTEGER NOT NULL, MESSAGE TEXT NOT NULL, AUTHOR TEXT NOT NULL);", channelname);
    }


    private String updateChannelMessageReq(String channelName, long date, String author, String Oldmsg, String newMsg) {
        return "UPDATE " + channelName +
                " SET MESSAGE='" + newMsg + "'"
                + " WHERE "
                + "DATE=" + date
                + " AND "
                + " MESSAGE LIKE '" + Oldmsg + "'"
                + " AND "
                + " AUTHOR LIKE '" + author + "'"
                + ";";

    }

    private String prepareInsertTwoValuesIntoTable(String tableName) {
        return "insert into " + Objects.requireNonNull(tableName) + " values (?, ?)";
    }

    private String prepareInsertThreeValuesIntoTable(String tableName) {
        return "insert into " + Objects.requireNonNull(tableName) + " values (?, ?, ?)";
    }


    /*CREATE AND UPDATE TABLES*/

    private void removeUserFromChanViewer(String channel, String toKick) throws SQLException {
        exeUpda(removeUserFromChanViewerRequest(channel, toKick));
    }

    private void updateChannelsTable(String channelName, String owner) throws SQLException {
        createPrepState(prepareInsertTwoValuesIntoTable("channels"));
        insertTwoValIntoTable(channelName, owner);
        executeRegisteredTask();
    }

    private void updateChanViewerTable(String channelName, String member) throws SQLException {
        createPrepState(prepareInsertTwoValuesIntoTable("chanviewer"));
        insertTwoValIntoTable(channelName, member);
        executeRegisteredTask();
    }

    private void insertTwoValIntoTable(String firstVal, String secondVal) throws SQLException {
        setPrepStringValue(1, firstVal, false);
        setPrepStringValue(2, secondVal, true);
    }


    private void insertDateMessageAuthor(long date, String message, String author) throws SQLException {
        setPrepLongValue(1, date, false);
        setPrepStringValue(2, message, false);
        setPrepStringValue(3, author, true);
    }



    /*CHECK CONSTRAINT*/

    private void recquirePositive(long l) {
        if (l < 0) {
            throw new IllegalArgumentException("Long must be > 0");
        }
    }

    //It's kind of dangerous to use a SQL query that could be change but, because it's on internal
    //use only and in a select only, this shouldn't be too risky for SQLInjection attacks
    private boolean canUserViewChannel(String channelName, String userName) throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM chanviewer WHERE MEMBER LIKE '" + userName + "'" +
                " AND CHANNAME LIKE '" + channelName + "';");
        if (rs.next()) {
            rs.close();
            return true;
        }
        rs.close();
        return false;
    }

    //It's kind of dangerous to use a SQL query that could be change but, because it's on internal
    //use only and in a select only, this shouldn't be too risky for SQLInjection attacks
    private boolean userCanControlAccessToChan(String channelName, String user) throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM channels WHERE CHANNAME LIKE '" + channelName + "'" +
                " AND OWNER LIKE '" + user + "';");
        if (rs.next()) {
            rs.close();
            return true;
        }
        rs.close();
        return false;
    }


}
