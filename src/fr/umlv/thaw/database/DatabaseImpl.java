package fr.umlv.thaw.database;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.server.Tools;
import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;

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
    DatabaseImpl(Path pathToDB, String dbName) throws ClassNotFoundException, SQLException {
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

            myDB.initializeDB();
        } catch (SQLException sql) {
            //nothing
        }
        HumanUser user1 = HumanUserFactory.createHumanUser("George", Tools.toSHA256("12345@A"));
        HumanUser user2 = HumanUserFactory.createHumanUser("TotoLeBus", Tools.toSHA256("TotoLeBus"));
        try {
            myDB.createLogin(user1);
            myDB.createLogin(user2);
        } catch (SQLException sql) {
            //ne rien faire car pas envie de planter sur une erreur
        }
        //La table chan1 étant supprimer à la fin, on peut la re créer sans risquer une erreur

        myDB.createChannelTable(chan1, "George");

        List<Channel> test = myDB.getchannelList();
        System.out.println("Nombre de channel : " + test.size());
        for (Channel chan : test) {
            System.out.println("Channel : " + chan);
        }
        Message m1 = MessageFactory.createMessage(user1, System.currentTimeMillis(), "Bonjour mon message");

        myDB.addMessageToChannelTable(chan1, m1);
        System.out.println("Messages dans Chan1 : ");
        System.out.println(myDB.messagesList(chan1));

        Message m2 = MessageFactory.createMessage(user2, System.currentTimeMillis(), "J'i bien h@ck la secu >: )");
        myDB.addMessageToChannelTable(chan1, m2);


        myDB.addUserToChan(chan1, "TotoLeBus", "George");
        Message m3 = MessageFactory.createMessage(user2, dte, "Avec les droits ça fonctionne mieux");
        myDB.addMessageToChannelTable(chan1, m3);


        System.out.println("Message dans Chan1 deuxième : ");
        myDB.messagesList(chan1).forEach(System.out::println);

        Message m4 = MessageFactory.createMessage(user2, System.currentTimeMillis(), "Ah je me suis planté et je vais me perdre :/");
        myDB.updateMessageFromChannel(chan1, m3, m4);

        System.out.println("Apres chgmt de message de TotoLeBus : ");
        System.out.println(myDB.messagesList(chan1));


        System.out.println("Avant remove George de Chan1");

        myDB.removeUserAccessToChan(chan1, "George", "George");

        System.out.println("George has been removed from Chan1");
        List<Channel> chans = myDB.getchannelList();
        System.out.println("Nombre de channel present après suppresion de l'auteur : " + chans.size());

        ResultSet rs = myDB.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

        System.out.println("Liste des tables présentent : ");
        while (rs.next()) {
            System.out.println("name : " + rs.getString(1));
        }
        System.out.println("Liste d'utilisateur : ");
        List<HumanUser> users = myDB.usersList();
        users.forEach(l -> System.out.println("user : " + l));


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
    public void initializeDB() throws SQLException {
        this.exeUpda(createUsersTableRequest());
        this.createChannelsTable();
        this.createChanViewerTable();
    }


    @Override
    public void createLogin(HumanUser humanUser) throws NoSuchAlgorithmException, SQLException {
        Objects.requireNonNull(humanUser);
        String login = humanUser.getName();
        String cryptPass = humanUser.getPasswordHash();
        createPrepState(prepareInsertTwoValuesIntoTable("users"));
        insertTwoValIntoTable(login, cryptPass);
        executeRegisteredTask();
    }

    @Override
    public void createChannelTable(String channelName, String owner) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(owner);
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
            List<HumanUser> toEject = retrieveUsersFromChan(channel);
            for (HumanUser user : toEject) {
                createPrepState(removeUserFromChanViewerRequest(channel, user.getName()));
                prepExecuteUpdate();
            }
            createPrepState(removeChannelFromChannelsRequest(channel, toKick));
            prepExecuteUpdate();
            createPrepState(removeChannel(channel));
            prepExecuteUpdate();
        }
    }

    @Override
    public void addMessageToChannelTable(String channelName, Message msg) throws SQLException {
        Objects.requireNonNull(channelName);
        //recquirePositive(date);
        Objects.requireNonNull(msg);
        //Objects.requireNonNull(author);
        if (canUserViewChannel(channelName, msg.getSender().getName())) {
            createPrepState(prepareInsertThreeValuesIntoTable(channelName));
            insertDateMessageAuthor(msg.getDate(), msg.getContent(), msg.getSender().getName());
            executeRegisteredTask();
        }
    }

    @Override
    public void updateMessageFromChannel(String channelName, Message oldMsg, Message newMsg) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(oldMsg);
        recquirePositive(oldMsg.getDate());
        Objects.requireNonNull(newMsg);
        if (canUserViewChannel(channelName, oldMsg.getSender().getName()) && oldMsg.getSender().equals(newMsg.getSender())) {
            exeUpda(updateChannelMessageReq(channelName, oldMsg.getDate(), newMsg.getSender().getName(), oldMsg.getContent(), newMsg.getContent()));
            executeRegisteredTask();
        }
    }

    @Override
    public List<HumanUser> usersList() throws SQLException {
        ResultSet rs = executeQuery("select * from users");
        List<HumanUser> userList = new ArrayList<>();
        HumanUser hum;
        while (rs.next()) {
            hum = HumanUserFactory.createHumanUser(rs.getString("LOGIN"), rs.getString("PSWD"));
            userList.add(hum);
        }
        rs.close();
        if (userList.isEmpty()) {
            return Collections.emptyList();
        }
        return userList;
    }


    @Override
    public List<HumanUser> retrieveUsersFromChan(String channel) throws SQLException {
        ResultSet rs = executeQuery(retriveUserFromChannelsRequest(Objects.requireNonNull(channel)));
        final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ? ;";
        List<HumanUser> users = new ArrayList<>();
        HumanUser tmpUser;
        String name;
        while (rs.next()) {
            name = rs.getString("MEMBER");
            prep = co.prepareStatement(request);
            prep.setString(1, name);
            if (prep.execute()) {
                try (ResultSet tmp = prep.getResultSet()) {
                    tmpUser = HumanUserFactory.createHumanUser(name, tmp.getString("PSWD"));
                    users.add(tmpUser);
                }
            }
        }
        rs.close();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }


    @Override
    public List<Message> messagesList(String channelName) throws SQLException {
        final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ? ;";
        ResultSet rs = executeQuery("SELECT * FROM " + channelName + " ;");
        List<Message> msgs = new ArrayList<>();
        HumanUser tmpUser;
        Message tmpMessage;
        while (rs.next()) {
            String author = rs.getString("AUTHOR");
            String message = rs.getString("MESSAGE");
            long date = rs.getLong("DATE");
            prep = co.prepareStatement(request);
            prep.setString(1, author);
            if (prep.execute()) {
                try (ResultSet tmp = prep.getResultSet()) {
                    tmpUser = HumanUserFactory.createHumanUser(author, tmp.getString("PSWD"));
                    tmpMessage = MessageFactory.createMessage(tmpUser, date, message);
                    msgs.add(tmpMessage);
                }
            }
        }
        rs.close();
        return Collections.unmodifiableList(msgs);
    }

    @Override
    public List<Channel> getchannelList() {
        ResultSet rs;
        Channel tmpChan;
        try {
            rs = executeQuery("SELECT * FROM CHANNELS;");
        } catch (SQLException sql) {
            //no result have been found we must return an empty list
            return Collections.emptyList();
        }
        List<Channel> channels = new ArrayList<>();
        try {
            while (rs.next()) {
                String channame = rs.getString("CHANNAME");
                String owner = rs.getString("OWNER");
                ResultSet tmp = executeQuery("SELECT PSWD FROM USERS WHERE LOGIN LIKE '" + owner + "';");
                while (tmp.next()) {
                    tmpChan = ChannelFactory.createChannel(HumanUserFactory.createHumanUser(owner, tmp.getString(1)), channame);
                    channels.add(tmpChan);
                }
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

    private void createChanViewerTable() throws SQLException {
        exeUpda(createChanViewerTableRequest());
    }


    private void createChannelsTable() throws SQLException {
        exeUpda(createChannelsTableRequest());
    }

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


    private boolean canUserViewChannel(String channelName, String userName) throws SQLException {
        final String request = "SELECT * FROM chanviewer WHERE MEMBER LIKE ?  AND CHANNAME LIKE ? ;";
        prep = co.prepareStatement(request);
        prep.setString(1, userName);
        prep.setString(2, channelName);
        if (prep.execute()) try (ResultSet tmp = prep.getResultSet()) {
            if (tmp.next()) {
                return true;
            }
        }
        return false;
    }


    private boolean userCanControlAccessToChan(String channelName, String user) throws SQLException {
        final String request = "SELECT * FROM channels WHERE CHANNAME LIKE ?  AND OWNER LIKE ? ;";
        prep = co.prepareStatement(request);
        prep.setString(1, channelName);
        prep.setString(2, user);
        if (prep.execute()) try (ResultSet tmp = prep.getResultSet()) {
            if (tmp.next()) {
                return true;
            }
        }
        return false;
    }


}
