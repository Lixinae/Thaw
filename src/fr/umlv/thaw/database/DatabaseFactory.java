package fr.umlv.thaw.database;


import java.nio.file.Path;
import java.sql.SQLException;

public class DatabaseFactory {
    public static Database createDatabase(Path path, String dbName) throws SQLException, ClassNotFoundException {
        return new DatabaseImpl(path, dbName);
    }
}
