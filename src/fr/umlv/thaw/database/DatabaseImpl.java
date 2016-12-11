package fr.umlv.thaw.database;


import fr.umlv.thaw.server.Tools;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Objects;

public class DatabaseImpl implements Database {

    private final String dbName;
    private final LinkedList<String> channelsName = new LinkedList<>();
    private final Class sqlite = Class.forName("org.sqlite.JDBC");//to load sqlite.jar
    private final String forGetConnection;
    private final Connection co;
    private final Statement state;
    private PreparedStatement prep;

    public DatabaseImpl(String dbName) throws ClassNotFoundException, SQLException {
        this.dbName = Objects.requireNonNull(dbName);
        forGetConnection = "jdbc:sqlite:" + dbName + ".db";
        co = createConnection();
        state = createStatement();
    }


    /*CONSTRUCTOR'S TOOLS*/

    //TODO Ne pas oublier de virer le main une fois que l'on aura fait toutes
    // les methodes necessaire
    public static void main(String[] args) throws Exception {
        DatabaseImpl myDB = new DatabaseImpl("MonTest");//Creation base du fichier toto.db
        myDB.exeUpda("drop table if exists people;");
        myDB.exeUpda("create table people (name, occupation, date);");
        myDB.createPrepState(
                "insert into people values (?, ?, ?);");/*On dit a la bdd de preparer une requete
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
        myDB.setPrepStringValue(1, "Gandhi", false);
        myDB.setPrepStringValue(2, "politics", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
        myDB.setPrepStringValue(1, "Turing", false);
        myDB.setPrepStringValue(2, "computers", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
        myDB.setPrepStringValue(1, "Wittgenstein", false);
        myDB.setPrepStringValue(2, "smartypants", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);


        //Prend les taches en attentes et les executes
        myDB.executeRegisteredTask();

        //Si jamais tu souhaites effectuer une requete ne modifiant pas la base tel qu'un select,
        //alors on stocke cela dans un objet nommee ResultSet que l'on obtient apres
        //appel a la methode executeQuery
        ResultSet rs = myDB.executeQuery("select * from people;");
        //L'objet ResulSet est un Iterator est donc doit donc le parcourir ainsi
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name"));
            System.out.println("job = " + rs.getString("occupation"));
            System.out.println("date = " + rs.getString("date"));
        }

        //test de createLogin
        myDB.createLogin("George", "12345@A");
        myDB.createLogin("TotoLeBus", "TotoLeBus");
        rs = myDB.executeQuery("select * from users");
        while (rs.next()) {
            System.out.println("login : " + rs.getString("LOGIN"));
            System.out.println("pswd  : " + rs.getString("PSWD"));
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
    public void createPrepState(String query) throws SQLException {
        Objects.requireNonNull(query);
        prep = co.prepareStatement(query);
    }

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

    public void setPrepDateValue(int idx, Date date, boolean addToBatch) throws SQLException {
        if (idx <= 0) {
            throw new IllegalArgumentException("idx must be > 0");
        }
        prep.setDate(idx, date);
        if (addToBatch) {
            prep.addBatch();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }

    public void exeUpda(String query) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    @Override
    public void createLogin(String login, String password) throws NoSuchAlgorithmException, SQLException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        String cryptPass = Tools.sha256ToString(Tools.hashToSha256(password));
        exeUpda(createUsersTableRequest().toString());
        createPrepState(prepareInsertLogPswdIntoTable());
        insertLogPswIntoTable(login, cryptPass);
        executeRegisteredTask();
    }

    public void executeRegisteredTask() throws SQLException {
        setAutoCommit(false);
        exeBatch();
        setAutoCommit(true);
    }

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

    private StringBuilder createUsersTableRequest() {
        StringBuilder createRequest = new StringBuilder();
        createRequest.append("create table if not exists users(")
                .append("LOGIN TEXT NOT NULL, ")
                .append("PSWD TEXT NOT NULL, ")
                .append("CONSTRAINT uniq UNIQUE(LOGIN)")
                .append(");");
        return createRequest;
    }

    private String prepareInsertLogPswdIntoTable() {
        return "insert into users values (?, ?)";
    }

    private void insertLogPswIntoTable(String login, String cryptPass) throws SQLException {
        setPrepStringValue(1, login, false);
        setPrepStringValue(2, cryptPass, true);
    }
}
