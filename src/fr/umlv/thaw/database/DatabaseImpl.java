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
        String forGetConnection = "jdbc:sqlite:" + pathToDB + FileSystems.getDefault().getSeparator() + dbName + ".db";
        co = DriverManager.getConnection(forGetConnection);
        Objects.requireNonNull(co);
        state = co.createStatement();
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


        String channelName = "Chan1";
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

        myDB.createChannelTable(channelName, "George");

        List<Channel> test = myDB.getChannelList();
        System.out.println("Nombre de channel : " + test.size());
        for (Channel chan : test) {
            System.out.println("Channel : " + chan);
        }
        Message m1 = MessageFactory.createMessage(user1, System.currentTimeMillis(), "Bonjour mon message");

        myDB.addMessageToChannelTable(channelName, m1);
        System.out.println("Messages dans Chan1 : ");
        System.out.println(myDB.getMessagesList(channelName));

        Message m2 = MessageFactory.createMessage(user2, System.currentTimeMillis(), "J'i bien h@ck la secu >: )");
        myDB.addMessageToChannelTable(channelName, m2);


        myDB.addUserToChan(channelName, "TotoLeBus", "George");
        Message m3 = MessageFactory.createMessage(user2, dte, "Avec les droits ça fonctionne mieux");
        myDB.addMessageToChannelTable(channelName, m3);


        System.out.println("Message dans Chan1 deuxième : ");
        myDB.getMessagesList(channelName).forEach(System.out::println);

//        Message m4 = MessageFactory.createMessage(user2, System.currentTimeMillis(), "Ah je me suis planté et je vais me perdre :/");
//        myDB.updateMessageFromChannel(channelName, m3, m4);

        System.out.println("Apres chgmt de message de TotoLeBus : ");
        System.out.println(myDB.getMessagesList(channelName));

        myDB.removeUserAccessToChan(channelName, "TotoLeBus", "George");
        System.out.println("TotoLeBus n'a plus acces a " + channelName);
        System.out.println("Avant remove George de Chan1");

        System.out.println("uti present : ");
        System.out.println(myDB.getUsersListFromChan(channelName));

        myDB.removeUserAccessToChan(channelName, "George", "George");

        System.out.println("George has been removed from Chan1");
        List<Channel> chans = myDB.getChannelList();
        System.out.println("Nombre de channel present après suppresion de l'auteur : " + chans.size());

        ResultSet rs = myDB.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

        System.out.println("Liste des tables présentent : ");
        while (rs.next()) {
            System.out.println("name : " + rs.getString(1));
        }
        System.out.println("Liste d'utilisateur : ");
        List<HumanUser> users = myDB.getAllUsersList();
        users.forEach(l -> System.out.println("user : " + l));


        //Ne pas oublier ensuite de fermer notre bdd et le ResultSet precedemment ouvert.
        //toujours fermer la bdd en dernier sous peine d'erreur

        rs.close();
        myDB.close();
    }

    /*
    * Public's method
    * */

    @Override
    public void initializeDB() throws SQLException {
        final String query = createUsersTableRequest();
        exeUpda(query);
        createChannelsTable();
        createChanViewerTable();
    }


    @Override
    public void createLogin(HumanUser humanUser) throws NoSuchAlgorithmException, SQLException {
        Objects.requireNonNull(humanUser);
        String login = humanUser.getName();
        String cryptPass = humanUser.getPasswordHash();
        final String query = prepareInsertTwoValuesIntoTable("users");
        prep = co.prepareStatement(query);
        insertTwoValIntoTable(login, cryptPass);
        executeRegisteredTask();
    }

    @Override
    public void createChannelTable(String channelName, String owner) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(owner);
        try {
            prep = co.prepareStatement(String.format("create table if not exists '%s' (" +
                    "DATE INTEGER NOT NULL, " +
                    "MESSAGE TEXT NOT NULL, " +
                    "AUTHOR TEXT NOT NULL );", channelName));
            prep.executeUpdate();

        } catch (SQLException sql) {
            System.err.println("Table " + channelName + " already exist");
            sql.printStackTrace();
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
    public void removeUserAccessToChan(String channelName, String userNametoKick, String authorityName) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(userNametoKick);
        Objects.requireNonNull(authorityName);
        if (userCanControlAccessToChan(channelName, authorityName) && !userNametoKick.equals(authorityName)) {
            // todo -> FindBugs
            final String query = "DELETE FROM CHANVIEWER WHERE "
                    + "CHANNAME LIKE ?"
                    + " AND MEMBER LIKE ? ;";

            prep = co.prepareStatement(query);
            prep.setString(1, channelName);
            prep.executeUpdate();
        } else if (userCanControlAccessToChan(channelName, authorityName) && userNametoKick.equals(authorityName)) {
            List<HumanUser> toEject = getUsersListFromChan(channelName);
            final String query = "DELETE FROM CHANVIEWER WHERE "
                    + "CHANNAME LIKE ? "
                    + " AND MEMBER LIKE ? ;";
            prep = co.prepareStatement(query);
            for (HumanUser user : toEject) {
                prep.setString(1, channelName);
                prep.setString(2, user.getName());
                prep.executeUpdate();
            }
            // todo -> findBugs
            final String query2 = "DELETE FROM CHANNELS WHERE "
                    + "CHANNAME LIKE ?  "
                    + " AND OWNER LIKE ? ;";
            prep = co.prepareStatement(query2);
            prep.setString(1, channelName);
            prep.setString(2, userNametoKick);
            prep.executeUpdate();
            // todo -> findBugs
//            final String query2 = "DROP TABLE IF EXISTS ? ;";
            prep = co.prepareStatement(String.format("DROP TABLE IF EXISTS %s", channelName));
            prep.executeUpdate();
        }
    }

    @Override
    public void addMessageToChannelTable(String channelName, Message msg) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(msg);
        if (canUserViewChannel(channelName, msg.getSender().getName())) {
            System.out.println("addMessageToChannelTable after if");
//            String query = prepareInsertThreeValuesIntoTable(channelName);
            System.out.println("Après prepareInsertThreeValue");
            prep = co.prepareStatement(String.format("insert into '%s' values (?, ?, ?)", channelName));
            System.out.println("After prep stae");
            insertDateMessageAuthor(msg.getDate(), msg.getContent(), msg.getSender().getName());
            executeRegisteredTask();
        }
    }

    @Override
    public List<HumanUser> getAllUsersList() throws SQLException {
        ResultSet rs = executeQuery("select * from users");
        List<HumanUser> userList = new ArrayList<>();
        HumanUser hum;
        String login;
        String pswd;
        while (rs.next()) {
            login = rs.getString("LOGIN");
            pswd = rs.getString("PSWD");
            hum = HumanUserFactory.createHumanUser(login, pswd);
            userList.add(hum);
        }
        rs.close();
        if (userList.isEmpty()) {
            return Collections.emptyList();
        }
        return userList;
    }


    @Override
    public List<HumanUser> getUsersListFromChan(String channelName) throws SQLException {
        Objects.requireNonNull(channelName);
        // todo -> FindBugs
        final String query = "SELECT MEMBER FROM CHANVIEWER WHERE CHANNAME LIKE ? ;";
        PreparedStatement p2 = co.prepareStatement(query);
        p2.setString(1, channelName);
        final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ? ;";
        List<HumanUser> users = new ArrayList<>();
        HumanUser tmpUser;
        String name;
        prep = co.prepareStatement(request);
        if (p2.execute()) {
            try (ResultSet rs = p2.getResultSet()) {
                name = rs.getString("MEMBER");
                prep.setString(1, name);
                if (prep.execute()) {
                    try (ResultSet tmp = prep.getResultSet()) {
                        tmpUser = HumanUserFactory.createHumanUser(name, tmp.getString("PSWD"));
                        users.add(tmpUser);
                    }
                }
            }
        }
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }


    // todo
    @Override
    public List<Message> getMessagesList(String channelName) throws SQLException {
        final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ? ;";
        System.out.println("Message list avant execute query");
        // todo FIND BUGS
        ResultSet rs = executeQuery("SELECT * FROM '" + channelName + "' ;");
//        ResultSet rs = executeQuery("SELECT * FROM 'default';");
        // J'ai commencer mais je vois pas comment continuer pour corriger le truc
//        System.out.println("channel name = "+channelName);
//        final String query = "SELECT * FROM 'default' ;";
//        prep = co.prepareStatement(query);
////        prep.setString(1, channelName);
//        if (prep.execute()) {
//            try (ResultSet tmp = prep.getResultSet()) {
//                String author = tmp.getString("AUTHOR");
//                String message = tmp.getString("MESSAGE");
//                long date = tmp.getLong("DATE");
//
//                System.out.println("author "+ author);
//            }
//        }

        List<Message> msgs = new ArrayList<>();
        HumanUser tmpUser;
        Message tmpMessage;
        prep = co.prepareStatement(request);
        while (rs.next()) {
            String author = rs.getString("AUTHOR");
            String message = rs.getString("MESSAGE");
            long date = rs.getLong("DATE");

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
    public List<Channel> getChannelList() {
        ResultSet rs;
        Channel tmpChan;
        try {
            rs = executeQuery("SELECT * FROM CHANNELS;");
        } catch (SQLException sql) {
            //no result have been found we must return an empty list
            return new ArrayList<>();
        }
        List<Channel> channels = new ArrayList<>();
        try {
            final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ?;";
            prep = co.prepareStatement(request);
            while (rs.next()) {
                String channame = rs.getString("CHANNAME");
                String owner = rs.getString("OWNER");
                prep.setString(1, owner);
                if (prep.execute()) {
                    try (ResultSet tmp = prep.getResultSet()) {
                        while (tmp.next()) {
                            tmpChan = ChannelFactory.createChannel(HumanUserFactory.createHumanUser(owner, tmp.getString(1)), channame);
                            channels.add(tmpChan);
                        }
                    }
                }

            }
            rs.close();
        } catch (SQLException sql) {
            throw new AssertionError("A database error has been occurred");
        }
        if (channels.isEmpty()) {
            return new ArrayList<>();
        }
        return channels;
    }

    @Override
    public void close() throws SQLException {
        co.close();
    }


    /*PRIVATE METHODS*/

    private void createChanViewerTable() throws SQLException {
        final String query = createChanViewerTableRequest();
        exeUpda(query);
    }


    private void createChannelsTable() throws SQLException {
        final String query = createChannelsTableRequest();
        exeUpda(query);
    }


    private void executeRegisteredTask() throws SQLException {
        co.setAutoCommit(false);
        prep.executeBatch();
        co.setAutoCommit(true);
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


    private ResultSet executeQuery(String query) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }

    private void exeUpda(String query) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    /* CREATION AND UPDATE REQUEST WITH SQL SYNTAX */
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


    // Tu passe toujours des constantes, donc j'ai laisser la fonction, car pas de bug( vu que tu donne que des constantes en parametre )
    private String prepareInsertTwoValuesIntoTable(String tableName) {
        return "insert into " + Objects.requireNonNull(tableName) + " values (?, ?)";
    }



    /*CREATE AND UPDATE TABLES*/


    private void updateChannelsTable(String channelName, String owner) throws SQLException {
//        createPrepState(prepareInsertTwoValuesIntoTable("channels"));
        final String query = prepareInsertTwoValuesIntoTable("channels");
        prep = co.prepareStatement(query);
        insertTwoValIntoTable(channelName, owner);
        executeRegisteredTask();
    }

    private void updateChanViewerTable(String channelName, String member) throws SQLException {
//        createPrepState(prepareInsertTwoValuesIntoTable("chanviewer"));
        final String query = prepareInsertTwoValuesIntoTable("chanviewer");
        prep = co.prepareStatement(query);
        insertTwoValIntoTable(channelName, member);
        executeRegisteredTask();
    }

    private void insertTwoValIntoTable(String firstVal, String secondVal) throws SQLException {
        setPrepStringValue(1, firstVal, false);
        setPrepStringValue(2, secondVal, true);
    }

    private void insertDateMessageAuthor(long date, String message, String author) throws SQLException {
//        setPrepLongValue(date);
        prep.setLong(1, date);
        setPrepStringValue(2, message, false);
        setPrepStringValue(3, author, true);
    }



    /*CHECK CONSTRAINT*/

    private boolean canUserViewChannel(String channelName, String userName) throws SQLException {
        final String request = "SELECT * FROM chanviewer WHERE MEMBER LIKE ?  AND CHANNAME LIKE ? ;";
        prep = co.prepareStatement(request);
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

    private boolean userCanControlAccessToChan(String channelName, String user) throws SQLException {
        final String request = "SELECT * FROM channels WHERE CHANNAME LIKE ?  AND OWNER LIKE ? ;";
        prep = co.prepareStatement(request);
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
