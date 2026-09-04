import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central place for the SQLite connection and one-time database setup.
 * The database is stored as a single file, dentalclinic.db, created
 * automatically in the project folder the first time the app runs.
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:dentalclinic.db";

    static {
        // Explicitly load the driver class. Modern JDBC drivers normally
        // self-register, but forcing this here means a missing/incorrect
        // classpath fails fast with a clear ClassNotFoundException instead
        // of the vaguer "No suitable driver found" from DriverManager.
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "SQLite JDBC driver class (org.sqlite.JDBC) was not found on the classpath. " +
                "Make sure the sqlite-jdbc jar is included when you compile and run " +
                "(e.g. java -cp .:lib/sqlite-jdbc-3.53.4.0.jar DentalClinicApp).", e);
        }
    }

    /** Opens a new connection. Callers are responsible for closing it (use try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Creates the required tables if they don't exist yet, and seeds default staff logins. */
    public static void initializeDatabase() {
        String createAppointments =
            "CREATE TABLE IF NOT EXISTS appointments (" +
            "  appointment_number INTEGER PRIMARY KEY," +
            "  patient_name       TEXT NOT NULL," +
            "  address            TEXT," +
            "  contact_number     TEXT," +
            "  dentist_name       TEXT," +
            "  treatment_type     TEXT," +
            "  appointment_date   TEXT," +
            "  appointment_time   TEXT" +
            ")";

        String createUsers =
            "CREATE TABLE IF NOT EXISTS users (" +
            "  username TEXT PRIMARY KEY," +
            "  password TEXT NOT NULL" +
            ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createAppointments);
            stmt.execute(createUsers);
            seedDefaultUsersIfEmpty(conn);

        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize database: " + e.getMessage(), e);
        }
    }

    private static void seedDefaultUsersIfEmpty(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, "admin");
                    ps.setString(2, "admin123");
                    ps.executeUpdate();

                    ps.setString(1, "receptionist");
                    ps.setString(2, "clinic2024");
                    ps.executeUpdate();
                }
            }
        }
    }
}
