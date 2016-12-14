package fr.umlv.thaw.database;


import fr.umlv.thaw.server.Tools;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

/**
 * This class represent an implementation of a Database
 * with the SQLITE driver
 */
public class DatabaseImpl implements Database {

    private final Path pathToDB;
    private final String dbName;
    private final LinkedList<String> channelsName = new LinkedList<>();
    private final Class sqlite = Class.forName("org.sqlite.JDBC");//to load sqlite.jar
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
        this.pathToDB = Objects.requireNonNull(pathToDB);
        this.dbName = Objects.requireNonNull(dbName);
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
        try {
            myDB = new DatabaseImpl(Paths.get("../db"), "mafia");//Creation base du fichier mafia.db
        } catch (SQLException sql) {
            System.err.println("can't open the db ");
            return;
        }
//        myDB.exeUpda("drop table if exists people;");
//        myDB.exeUpda("create table people (name, occupation, date);");
//        myDB.createPrepState(
//                "insert into people values (?, ?, ?);");
//
            /*On dit a la bdd de preparer une requete
                insert avec trois valeurs que l'on specifiera en dessous avec un setPrep
                Grossomodo : le prep sert a stocker une requête dans laquelle tu as pas encore specifier
                la valeur des param
                */

        /*Dans cet exemple, on a 3 parametre :
        * un nom, une occupation et une date on a specifier nulle part le type, on le fixera
        * lors de la premiere asignation donc.
        *
        * Faire appel a une des methodes setPrep permet donc :
        * => de preciser ou je met la valeur dans la requete que j'ai prepare
        * => quelle est la valeur
        * => quel est le type de la valeur (date ou string dans ce cas qui seront converti en un type correct
        * pour la bdd)
        * => est-ce que je peux enregistre ma requete (si oui alors j'ecris la requete dans un objet en memoire qui se
        * souviendra des diferentes requetes efectuees)
        * */
        /*Bien sur, dans la pratique, on tachera de regrouper nos requetes en une methode
        * cf : createLogin
        * */
//        myDB.setPrepStringValue(1, "Gandhi", false);
//        myDB.setPrepStringValue(2, "politics", false);
//        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
//        myDB.setPrepStringValue(1, "Turing", false);
//        myDB.setPrepStringValue(2, "computers", false);
//        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
//        myDB.setPrepStringValue(1, "Wittgenstein", false);
//        myDB.setPrepStringValue(2, "smartypants", false);
//        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
//
//
//        //Prend les taches en attentes et les executes
//        myDB.executeRegisteredTask();
//
//        //Si jamais tu souhaites effectuer une requete ne modifiant pas la base tel qu'un select,
//        //alors on stocke cela dans un objet nommee ResultSet que l'on obtient apres
//        //appel a la methode executeQuery
//        rs = myDB.executeQuery("select * from people;");
//        //L'objet ResulSet est un Iterator est donc doit donc le parcourir ainsi
//        while (rs.next()) {
//            System.out.println("name = " + rs.getString("name"));
//            System.out.println("job = " + rs.getString("occupation"));
//            System.out.println("date = " + rs.getString("date"));
//        }

        String chan1 = "Chan1";
        //test de createLogin
        try {
            myDB.createLogin("George", "12345@A");
            myDB.createLogin("TotoLeBus", "TotoLeBus");
        } catch (SQLException sql) {
            //ne rien faire car pas envie de planter sur une erreur
        }
        try {
            myDB.createChannelTable(chan1, "George");
        } catch (SQLException sql) {
            System.out.println("Pb a la creation");
        }
        System.out.println("Nombre de channel : " + myDB.channelList().size());
        myDB.addMessageToChannelTable(chan1, System.currentTimeMillis(), "Bonjour mon message", "George");
        for (String chan : myDB.channelList()) {
            System.out.println("Channel : " + chan);
        }
        System.out.println("Messages dans Chan1 : ");
        System.out.println(myDB.messagesList(chan1));


        myDB.addMessageToChannelTable(chan1, System.currentTimeMillis(), "J'i bien h@ck la secu >: )", "TotoLeBus");


        myDB.addUserToChan("Chan1", "TotoLeBus", "George");
        myDB.addMessageToChannelTable("Chan1", System.currentTimeMillis(), "Avec les droits ça fonctionne mieux", "TotoLeBus");


        System.out.println("Message dans Chan1 deuxième : ");
        System.out.println(myDB.messagesList("Chan1"));
        myDB.removeUserAccessToChan(chan1, "TotoLeBus", "George");

        //myDB.removeUserAccessToChan(chan1, "George", "George");
        List<String> chans = myDB.channelList();
        System.out.println("Nombre de channel present après suppresion de l'auteur : " + chans.size());
        //Ne pas oublier ensuite de fermer notre bdd et le ResultSet precedemment ouvert.
        //toujours fermer la bdd en dernier sous peine d'erreur


        //rs.close();
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
    public void createPrepState(String query) throws SQLException {
        Objects.requireNonNull(query);
        prep = co.prepareStatement(query);
    }

    @Override
    public void setPrepStringValue(int idx, String value, boolean addToBatch) throws SQLException {
        Objects.requireNonNull(value);
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setString(idx, value);
        if (addToBatch) {
            prep.addBatch();
        }
    }

    @Override
    public void setPrepLongValue(int idx, Long value, boolean addToBatch) throws SQLException {
        Objects.requireNonNull(value);
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setLong(idx, value);
        if (addToBatch) {
            prep.addBatch();
        }
    }


    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }

    @Override
    public void exeUpda(String query) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    @Override
    public void executeRegisteredTask() throws SQLException {
        setAutoCommit(false);
        exeBatch();
        setAutoCommit(true);
    }

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
        try {
            exeUpda(createChannelTableRequest(channelName));
        } catch (SQLException sql) {
            //The channel already exist, we don't need to go further
            throw new AssertionError("table " + channelName + " already exist");
        }
        updateChannelsTable(channelName, owner);
        updateChanViewerTable(channelName, owner);
    }

    @Override
    public void addUserToChan(String channel, String toAuthorized, String authority) throws SQLException {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(toAuthorized);
        Objects.requireNonNull(authority);
        if (userCanControlAccessToChan(channel, authority)) {
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
                executeQuery(removeUserFromChanViewerRequest(channel, toKick));
            }
            executeQuery(removeChannelFromChannelsRequest(channel, toKick));
            executeQuery(removeChannel(channel));
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


    private void setAutoCommit(boolean b) throws SQLException {
        co.setAutoCommit(b);
    }


    /* CREATION AND UPDATE REQUEST WITH SQL SYNTAX */

    private String removeChannel(String channel) {
        return "DROP TABLE IF EXISTS " + channel + " ;";
    }

    private String removeChannelFromChannelsRequest(String channel, String toKick) {
        return "DELETE FROM CHANNELS WHERE "
                + "CHANNAME LIKE \'" + channel + "\' "
                + " AND OWNER LIKE \'" + toKick + "\';";
    }


    private String removeUserFromChanViewerRequest(String channel, String toKick) {
        return "DELETE FROM CHANVIEWER WHERE "
                + "CHANNAME LIKE \'" + channel + "\' "
                + " AND MEMBER LIKE \'" + toKick + "\';";
    }


    private String retriveUserFromChannelsRequest(String channelName) {
        return "SELECT MEMBER FROM CHANVIEWER WHERE "
                + "CHANNAME LIKE \'" + channelName + "\';";
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
                " SET MESSAGE=\'" + newMsg + "\'"
                + " WHERE "
                + "DATE=" + date
                + " AND "
                + " MESSAGE LIKE \'" + Oldmsg + "\'"
                + " AND "
                + " AUTHOR LIKE \'" + author + "\'"
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
        exeUpda(createChannelsTableRequest());
        createPrepState(prepareInsertTwoValuesIntoTable("channels"));
        insertTwoValIntoTable(channelName, owner);
        executeRegisteredTask();
    }


    private void updateChanViewerTable(String channelName, String member) throws SQLException {
        exeUpda(createChanViewerTableRequest());
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

    private boolean canUserViewChannel(String channelName, String userName) throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM chanviewer WHERE MEMBER LIKE \'" + userName + "\'" +
                " AND CHANNAME LIKE \'" + channelName + "\';");
        if (rs.next()) {
            rs.close();
            return true;
        }
        rs.close();
        return false;
    }

    private boolean userCanControlAccessToChan(String channelName, String user) throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM channels WHERE CHANNAME LIKE \'" + channelName + "\'" +
                " AND OWNER LIKE \'" + user + "\';");
        if (rs.next()) {
            rs.close();
            return true;
        }
        rs.close();
        return false;
    }

}
