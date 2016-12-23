package fr.umlv.thaw.database;


import java.nio.file.Path;
import java.sql.SQLException;

/**
 * This class is a Static Factory for
 * DatabaseImpl
 */
public class DatabaseFactory {
    public static Database createDatabase(Path path) throws SQLException, ClassNotFoundException {
        return new DatabaseImpl(path, "database");
    }
}
