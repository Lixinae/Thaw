package fr.umlv.thaw.database;


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

    public static void main(String[] args) throws Exception {
        DatabaseImpl myDB = new DatabaseImpl("toto");//Creation base du fichier toto.db
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
        myDB.setPrepStringValue(1, "Gandhi", false);
        myDB.setPrepStringValue(2, "politics", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
        myDB.setPrepStringValue(1, "Turing", false);
        myDB.setPrepStringValue(2, "computers", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
        myDB.setPrepStringValue(1, "Wittgenstein", false);
        myDB.setPrepStringValue(2, "smartypants", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);

        /*Le setAutoCommit permet de dire si oui on non je dois automatiquement
        * commit la valeur même si il y a un probleme*/
        //on preferera mettre a false avant d'executer une suite de requete histoire de ne pas bouissiler l'integrite de
        //la bdd en cas de pb
        myDB.setAutoCommit(false);

        //J'execute la suite d'instruction retenu en memoire pour faire des insert ou autre operations pouvant
        //modifier la bdd
        myDB.exeBatch();

        //Maintenant je peux remettre ce autoCommit a vrai qui est le comportement par defaut
        myDB.setAutoCommit(true);

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

    public ResultSet executeQuery(String query) throws SQLException {
        Objects.requireNonNull(query);
        return state.executeQuery(query);
    }

    public void close() throws SQLException {
        co.close();
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

    public void exeBatch() throws SQLException {
        prep.executeBatch();
    }

    public void exeUpda(String query) throws SQLException {
        Objects.requireNonNull(query);
        state.executeUpdate(query);
    }

    public void setAutoCommit(boolean b) throws SQLException {
        co.setAutoCommit(b);
    }
}
