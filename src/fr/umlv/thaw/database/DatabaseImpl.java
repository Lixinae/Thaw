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
        DatabaseImpl myDB = new DatabaseImpl("toto");
        myDB.exeUpda("drop table if exists people;");
        myDB.exeUpda("create table people (name, occupation, date);");
        myDB.createPrepState(
                "insert into people values (?, ?, ?);");

        myDB.setPrepStringValue(1, "Gandhi", false);
        myDB.setPrepStringValue(2, "politics", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
        myDB.setPrepStringValue(1, "Turing", false);
        myDB.setPrepStringValue(2, "computers", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);
        myDB.setPrepStringValue(1, "Wittgenstein", false);
        myDB.setPrepStringValue(2, "smartypants", false);
        myDB.setPrepDateValue(3, Date.valueOf(LocalDate.now()), true);

        myDB.setAutoCommit(false);
        myDB.exeBatch();
        myDB.setAutoCommit(true);

        ResultSet rs = myDB.executeQuery("select * from people;");
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name"));
            System.out.println("job = " + rs.getString("occupation"));
            System.out.println("date = " + rs.getString("date"));
        }
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
        return state.executeQuery(query);
    }

    public void close() throws SQLException {
        co.close();
    }

    public void setPrepStringValue(int idx, String value, boolean addToBatch) throws SQLException {
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
        state.executeUpdate(query);
    }

    public void setAutoCommit(boolean b) throws SQLException {
        co.setAutoCommit(b);
    }
}
