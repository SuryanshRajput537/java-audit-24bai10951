import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:fittrack.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initializeTables();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() { return connection; }

    private void initializeTables() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS activities (" +
            "id            INTEGER PRIMARY KEY AUTOINCREMENT," +
            "activity_type TEXT NOT NULL," +
            "name          TEXT NOT NULL," +
            "duration_min  INTEGER NOT NULL," +
            "calories      REAL," +
            "log_date      TEXT NOT NULL," +
            "notes         TEXT," +
            "extra1        TEXT," +
            "extra2        TEXT)"
        );
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS daily_logs (" +
            "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
            "log_date    TEXT NOT NULL UNIQUE," +
            "water_ml    REAL DEFAULT 0," +
            "sleep_hours REAL DEFAULT 0," +
            "weight_kg   REAL DEFAULT 0)"
        );
        stmt.close();
    }

    public void close() {
        try { if (connection != null) connection.close(); }
        catch (SQLException e) { System.err.println("Error closing DB: " + e.getMessage()); }
    }
}
