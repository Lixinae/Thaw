package fr.umlv.thaw.database;


import fr.umlv.thaw.server.Tools;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
        DatabaseImpl myDB = new DatabaseImpl(Paths.get("../db"), "mafia");//Creation base du fichier mafia.db
        ResultSet rs;
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

        //test de createLogin
        try {
            myDB.createLogin("George", "12345@A");
            myDB.createLogin("TotoLeBus", "TotoLeBus");
        } catch (SQLException sql) {
            //ne rien faire car pas envie de planter sur une erreur
        }

//        System.out.println("--------------------------------------");
//        rs = myDB.executeQuery("select * from users");
//        while (rs.next()) {
//            System.out.println("login : " + rs.getString("LOGIN"));
//            System.out.println("pswd  : " + rs.getString("PSWD")); les mot de passe sont bien crypte
//        }

        System.out.println("TEST DE LISTE NOM : ");

        List<String> users = myDB.usersList();
        System.out.println("Et l'affichage : ");
        System.out.println(users);

        //test addMessageToChannelTable
        System.out.println("--------------------------------------");
        myDB.addMessageToChannelTable("Chan1", System.currentTimeMillis(), "je suis suuiiisssse", "un barbu");
        myDB.addMessageToChannelTable("Chan1", System.currentTimeMillis(), "je suis Marron", "un troll");
        rs = myDB.executeQuery("select * from Chan1");
        SimpleDateFormat form = new SimpleDateFormat("dd MMM yyyy HH: mm");
        while (rs.next()) {
            System.out.println("channel : Chan1");
            System.out.println("DATE : " + form.format(new Date(rs.getLong("DATE"))));
            System.out.println("MESSAGE  : " + rs.getString("MESSAGE"));
            System.out.println("AUTHOR  : " + rs.getString("AUTHOR"));
            System.out.println("........");
        }
        long d1 = System.currentTimeMillis();
        System.out.println("--------------------------------------");
        myDB.addMessageToChannelTable("SuperChannel", d1, "je suis ...", "foreveralone");
        myDB.addMessageToChannelTable("SuperChannel", System.currentTimeMillis(), "for ever alone :@", "foreveralone");

        System.out.println("NOUVEAU TEST!!!");
        System.out.println(myDB.messagesList("SuperChannel"));
        System.out.println("--------------------");
        System.out.println("Même test mais avec formattage !!!");
        String[] lines = myDB.messagesList("SuperChannel").split("\n");
        for (String line : lines) {
            StringBuilder sb = new StringBuilder();
            String[] tmp = line.split("0X00");
            sb.append(form.format(new Date(Long.parseLong(tmp[0]))));
            sb.append(" ");
            sb.append(tmp[1]);
            sb.append(" : ");
            sb.append(tmp[2]);
            System.out.println(sb);
        }
        System.out.println("Test channelList");
        System.out.println(myDB.channelList());

        //OK
//        System.out.println("--------------------------------------");
//        rs = myDB.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");//requete pour recuperer nom des tables cree
//        while (rs.next()) {
//            String req = rs.getString(1);
//            System.out.println("Table name : " + req);
//        }
        System.out.println("--------------------------------------");
        myDB.updateMessageFromChannel("SuperChannel", d1, "foreveralone", "je suis ...", "yésoui");
        rs = myDB.executeQuery("select * from SuperChannel");
        while (rs.next()) {
            System.out.println("channel : SuperChannel");
            System.out.println("DATE : " + form.format(new Date(rs.getLong("DATE"))));
            System.out.println("MESSAGE  : " + rs.getString("MESSAGE"));
            System.out.println("AUTHOR  : " + rs.getString("AUTHOR"));
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
    public void setPrepDateValue(int idx, Date date, boolean addToBatch) throws SQLException {
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setDate(idx, date);
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
//        String cryptPass = Tools.sha256ToString(Tools.hashToSha256(password));
        exeUpda(createUsersTableRequest());
        createPrepState(prepareInsertTwoValuesIntoTable());
        insertLogPswIntoTable(login, cryptPass);
        executeRegisteredTask();
    }

    @Override
    public void addMessageToChannelTable(String channelName, long date, String msg, String author) throws SQLException {
        Objects.requireNonNull(channelName);
        recquirePositive(date);
        Objects.requireNonNull(msg);
        Objects.requireNonNull(author);
        createChannelTable(channelName);
        createPrepState(prepareInsertThreeValuesIntoTable(channelName));
        insertDateMessageAuthor(date, msg, author);
        executeRegisteredTask();
    }

    @Override
    public void updateMessageFromChannel(String channelName, long date, String author, String Oldmsg, String newMsg) throws SQLException {
        Objects.requireNonNull(channelName);
        recquirePositive(date);
        Objects.requireNonNull(author);
        Objects.requireNonNull(Oldmsg);
        Objects.requireNonNull(newMsg);
        exeUpda(updateChannelMessageReq(channelName, date, author, Oldmsg, newMsg));
        executeRegisteredTask();
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
    public List<String> channelList() throws SQLException {
        ResultSet rs = executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
        List<String> channels = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString(1);
            if (!name.equals("users")) {
                channels.add(name);
            }
        }
        rs.close();
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

    private String createUsersTableRequest() {
        return "create table if not exists users(" +
                "LOGIN TEXT NOT NULL, " +
                "PSWD TEXT NOT NULL, " +
                "CONSTRAINT uniq UNIQUE(LOGIN)" +
                ");";
    }

    //TODO Gestion des droits
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

    private void createChannelTable(String channelName) throws SQLException {
        exeUpda(createChannelTableRequest(channelName));
    }


    private String prepareInsertTwoValuesIntoTable() {
        return "insert into users values (?, ?)";
    }

    private String prepareInsertThreeValuesIntoTable(String channelName) {
        return "insert into " + Objects.requireNonNull(channelName) + " values (?, ?, ?)";
    }

    private void insertLogPswIntoTable(String login, String cryptPass) throws SQLException {
        setPrepStringValue(1, login, false);
        setPrepStringValue(2, cryptPass, true);
    }

    private void insertDateMessageAuthor(long date, String message, String author) throws SQLException {
        setPrepLongValue(1, date, false);
        setPrepStringValue(2, message, false);
        setPrepStringValue(3, author, true);
    }


    private void recquirePositive(long l) {
        if (l < 0) {
            throw new IllegalArgumentException("Long must be > 0");
        }
    }
}
