import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Sunrise Dental Clinic - Appointment and Patient Management System
 *
 * Desktop (Swing) application backed by a SQLite database (dentalclinic.db).
 * Starts at the login window; on successful login the main tabbed window
 * opens with:
 *   1. Register New Appointment
 *   2. Display Appointment Details
 *   3. Calculate and Print Bill
 *   4. All Appointments (overview table)
 *   5. Help
 *   6. Exit (File menu or window close button)
 */
public class DentalClinicApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to the default cross-platform look and feel if this fails.
        }

        try {
            DatabaseHelper.initializeDatabase();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not start the database:\n" + e.getMessage() +
                    "\n\nMake sure the SQLite JDBC driver jar is on the classpath.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
