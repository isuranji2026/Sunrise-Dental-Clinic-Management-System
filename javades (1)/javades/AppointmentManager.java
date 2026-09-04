import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes appointment records in the SQLite database.
 * Public methods are unchanged from the text-file version, so MainFrame
 * did not need any changes when this class was rewritten.
 */
public class AppointmentManager {

    /** The next unique appointment number that will be assigned. */
    public int generateAppointmentNumber() {
        String sql = "SELECT MAX(appointment_number) AS max_num FROM appointments";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int max = rs.getInt("max_num");
                if (!rs.wasNull()) {
                    return max + 1;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error reading appointment numbers: " + e.getMessage());
        }
        return 1001; // first appointment number issued by the clinic
    }

    public void addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments " +
            "(appointment_number, patient_name, address, contact_number, " +
            " dentist_name, treatment_type, appointment_date, appointment_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointment.getAppointmentNumber());
            ps.setString(2, appointment.getPatientName());
            ps.setString(3, appointment.getAddress());
            ps.setString(4, appointment.getContactNumber());
            ps.setString(5, appointment.getDentistName());
            ps.setString(6, appointment.getTreatmentType());
            ps.setString(7, appointment.getAppointmentDate());
            ps.setString(8, appointment.getAppointmentTime());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error saving appointment: " + e.getMessage());
        }
    }

    public Appointment findByAppointmentNumber(int number) {
        String sql = "SELECT * FROM appointments WHERE appointment_number = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, number);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching for appointment: " + e.getMessage());
        }
        return null;
    }

    /** All stored appointments, ordered by appointment number, for the overview table. */
    public List<Appointment> getAllAppointments() {
        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_number";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error loading appointments: " + e.getMessage());
        }
        return results;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("appointment_number"),
            rs.getString("patient_name"),
            rs.getString("address"),
            rs.getString("contact_number"),
            rs.getString("dentist_name"),
            rs.getString("treatment_type"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time")
        );
    }
}
