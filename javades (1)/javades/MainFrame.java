import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Main application window shown after a successful login.
 * Organised as a tabbed interface:
 *   - Register New Appointment
 *   - Display Appointment Details
 *   - Calculate and Print Bill
 *   - All Appointments (bonus overview table)
 *   - Help
 */
public class MainFrame extends JFrame {

    private final AppointmentManager manager = new AppointmentManager();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private JTabbedPane tabbedPane;

    // Register tab
    private JLabel apptNumberValueLabel;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField contactField;
    private JTextField dentistField;
    private JComboBox<String> treatmentCombo;
    private JLabel feeValueLabel;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

    // Display tab
    private JTextField displaySearchField;
    private JTextArea displayResultArea;

    // Bill tab
    private JTextField billSearchField;
    private JTextArea billResultArea;
    private JButton printBillButton;
    private JButton saveBillButton;
    private Appointment currentBillAppointment;
    private double currentBillTotal;
    private double currentConsultationFee;
    private double currentTreatmentFee;

    // All appointments tab
    private DefaultTableModel tableModel;
    private JTable appointmentsTable;

    public MainFrame() {
        setTitle("Sunrise Dental Clinic - Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExit();
            }
        });

        setJMenuBar(buildMenuBar());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Register Appointment", buildRegisterPanel());
        tabbedPane.addTab("Display Details", buildDisplayPanel());
        tabbedPane.addTab("Calculate & Print Bill", buildBillPanel());
        tabbedPane.addTab("All Appointments", buildAllAppointmentsPanel());
        tabbedPane.addTab("Help", buildHelpPanel());

        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) {
                refreshAssignedAppointmentNumber();
            } else if (index == 3) {
                refreshAppointmentsTable();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ---------- Menu Bar ----------

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmExit());
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sunrise Dental Clinic\nAppointment & Patient Management System\nVersion 1.0",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the system?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ---------- Tab 1: Register New Appointment ----------

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Appointment Number:"), gbc);
        gbc.gridx = 1;
        apptNumberValueLabel = new JLabel();
        apptNumberValueLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(apptNumberValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        form.add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        form.add(addressField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        form.add(contactField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Dentist Name:"), gbc);
        gbc.gridx = 1;
        dentistField = new JTextField(20);
        form.add(dentistField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Treatment Type:"), gbc);
        gbc.gridx = 1;
        treatmentCombo = new JComboBox<>(TreatmentFees.getTreatmentNames());
        treatmentCombo.addActionListener(e -> updateFeeLabel());
        form.add(treatmentCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Treatment Fee:"), gbc);
        gbc.gridx = 1;
        feeValueLabel = new JLabel();
        form.add(feeValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Appointment Date:"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(new Date());
        form.add(dateSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Appointment Time:"), gbc);
        gbc.gridx = 1;
        timeSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        timeSpinner.setValue(new Date());
        form.add(timeSpinner, gbc);
        row++;

        updateFeeLabel();

        JButton registerButton = new JButton("Register Appointment");
        registerButton.addActionListener(e -> registerAppointment());

        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearRegisterForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);

        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshAssignedAppointmentNumber();
        return panel;
    }

    private void updateFeeLabel() {
        String treatment = (String) treatmentCombo.getSelectedItem();
        Double fee = TreatmentFees.getFee(treatment);
        feeValueLabel.setText(String.format("Rs. %.2f", fee == null ? 0.0 : fee));
    }

    private void refreshAssignedAppointmentNumber() {
        if (apptNumberValueLabel != null) {
            apptNumberValueLabel.setText(String.valueOf(manager.generateAppointmentNumber()));
        }
    }

    private void registerAppointment() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contact = contactField.getText().trim();
        String dentist = dentistField.getText().trim();
        String treatment = (String) treatmentCombo.getSelectedItem();

        if (name.isEmpty() || address.isEmpty() || dentist.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient name, address, and dentist name cannot be empty.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!contact.matches("\\d{9,15}")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid contact number (digits only, 9-15 digits).",
                    "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentNumber = manager.generateAppointmentNumber();
        String date = dateFormat.format((Date) dateSpinner.getValue());
        String time = timeFormat.format((Date) timeSpinner.getValue());

        Appointment appointment = new Appointment(appointmentNumber, name, address,
                contact, dentist, treatment, date, time);
        manager.addAppointment(appointment);

        JOptionPane.showMessageDialog(this,
                "Appointment registered successfully!\n\n" + appointment.toDetailsText(),
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

        clearRegisterForm();
    }

    private void clearRegisterForm() {
        nameField.setText("");
        addressField.setText("");
        contactField.setText("");
        dentistField.setText("");
        treatmentCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        timeSpinner.setValue(new Date());
        refreshAssignedAppointmentNumber();
    }

    // ---------- Tab 2: Display Appointment Details ----------

    private JPanel buildDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Appointment Number:"));
        displaySearchField = new JTextField(10);
        searchPanel.add(displaySearchField);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        displayResultArea = new JTextArea();
        displayResultArea.setEditable(false);
        displayResultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        searchButton.addActionListener(e -> searchAppointment());
        displaySearchField.addActionListener(e -> searchAppointment());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(displayResultArea), BorderLayout.CENTER);
        return panel;
    }

    private void searchAppointment() {
        Integer number = parseAppointmentNumber(displaySearchField.getText());
        if (number == null) {
            return;
        }
        Appointment appt = manager.findByAppointmentNumber(number);
        if (appt == null) {
            displayResultArea.setText("No appointment found with number " + number + ".");
        } else {
            displayResultArea.setText(appt.toDetailsText());
        }
    }

    // ---------- Tab 3: Calculate and Print Bill ----------

    private JPanel buildBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Appointment Number:"));
        billSearchField = new JTextField(10);
        searchPanel.add(billSearchField);
        JButton calcButton = new JButton("Calculate Bill");
        searchPanel.add(calcButton);

        billResultArea = new JTextArea();
        billResultArea.setEditable(false);
        billResultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        calcButton.addActionListener(e -> calculateBill());
        billSearchField.addActionListener(e -> calculateBill());

        saveBillButton = new JButton("Save Receipt to File");
        saveBillButton.setEnabled(false);
        saveBillButton.addActionListener(e -> saveReceiptToFile());

        printBillButton = new JButton("Print Bill");
        printBillButton.setEnabled(false);
        printBillButton.addActionListener(e -> printBill());

        JPanel bottomButtons = new JPanel();
        bottomButtons.add(saveBillButton);
        bottomButtons.add(printBillButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(billResultArea), BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);
        return panel;
    }

    private void calculateBill() {
        Integer number = parseAppointmentNumber(billSearchField.getText());
        if (number == null) {
            return;
        }
        Appointment appt = manager.findByAppointmentNumber(number);
        if (appt == null) {
            billResultArea.setText("No appointment found with number " + number + ".");
            saveBillButton.setEnabled(false);
            printBillButton.setEnabled(false);
            currentBillAppointment = null;
            return;
        }

        Double treatmentFeeObj = TreatmentFees.getFee(appt.getTreatmentType());
        double treatmentFee = (treatmentFeeObj == null) ? 0.0 : treatmentFeeObj;
        double consultationFee = TreatmentFees.CONSULTATION_FEE;
        double total = treatmentFee + consultationFee;

        currentBillAppointment = appt;
        currentTreatmentFee = treatmentFee;
        currentConsultationFee = consultationFee;
        currentBillTotal = total;

        billResultArea.setText(buildBillText(appt, consultationFee, treatmentFee, total));
        saveBillButton.setEnabled(true);
        printBillButton.setEnabled(true);
    }

    private String buildBillText(Appointment appt, double consultationFee,
                                  double treatmentFee, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("============ SUNRISE DENTAL CLINIC ============\n");
        sb.append("                 PATIENT BILL\n");
        sb.append("=================================================\n");
        sb.append("Appointment Number  : ").append(appt.getAppointmentNumber()).append("\n");
        sb.append("Patient Name        : ").append(appt.getPatientName()).append("\n");
        sb.append("Dentist Name        : ").append(appt.getDentistName()).append("\n");
        sb.append("Treatment Type      : ").append(appt.getTreatmentType()).append("\n");
        sb.append("Appointment Date    : ").append(appt.getAppointmentDate()).append("\n");
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("Consultation Fee     : Rs. %.2f%n", consultationFee));
        sb.append(String.format("Treatment Fee        : Rs. %.2f%n", treatmentFee));
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT DUE     : Rs. %.2f%n", total));
        sb.append("=================================================\n");
        return sb.toString();
    }

    private void saveReceiptToFile() {
        if (currentBillAppointment == null) {
            return;
        }
        String fileName = "receipt_" + currentBillAppointment.getAppointmentNumber() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.print(buildBillText(currentBillAppointment, currentConsultationFee,
                    currentTreatmentFee, currentBillTotal));
            JOptionPane.showMessageDialog(this,
                    "Receipt saved as " + fileName,
                    "Receipt Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save receipt: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printBill() {
        try {
            boolean printed = billResultArea.print();
            if (!printed) {
                JOptionPane.showMessageDialog(this, "Printing was cancelled.",
                        "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not print: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Tab 4: All Appointments (overview table) ----------

    private JPanel buildAllAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"Appt #", "Patient", "Dentist", "Treatment", "Date", "Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setFillsViewportHeight(true);

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> refreshAppointmentsTable());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("All registered appointments:"), BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshAppointmentsTable() {
        tableModel.setRowCount(0);
        List<Appointment> all = manager.getAllAppointments();
        for (Appointment appt : all) {
            tableModel.addRow(new Object[]{
                    appt.getAppointmentNumber(),
                    appt.getPatientName(),
                    appt.getDentistName(),
                    appt.getTreatmentType(),
                    appt.getAppointmentDate(),
                    appt.getAppointmentTime()
            });
        }
    }

    // ---------- Tab 5: Help ----------

    private JPanel buildHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea helpArea = new JTextArea();
        helpArea.setEditable(false);
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        helpArea.setText(
                "Welcome to the Sunrise Dental Clinic System!\n\n" +
                "1. Register Appointment\n" +
                "   Open this tab to add a new patient appointment. The system\n" +
                "   automatically assigns a unique appointment number, shown at\n" +
                "   the top of the form. Fill in the patient's details, choose a\n" +
                "   dentist, pick a treatment type, and set the date and time.\n\n" +
                "2. Display Details\n" +
                "   Enter an appointment number and click Search to view the\n" +
                "   patient's complete record.\n\n" +
                "3. Calculate & Print Bill\n" +
                "   Enter an appointment number and click Calculate Bill to see\n" +
                "   the consultation fee, treatment fee, and total due. You can\n" +
                "   save the bill as a text file receipt or send it to a printer.\n\n" +
                "4. All Appointments\n" +
                "   Shows every appointment on record in a table. Click Refresh\n" +
                "   List after registering a new appointment to update the view.\n\n" +
                "5. Exit\n" +
                "   Use File > Exit, or close the window, to safely quit. All data\n" +
                "   is saved to disk immediately after each registration, so\n" +
                "   nothing is lost on exit.\n"
        );

        panel.add(new JScrollPane(helpArea), BorderLayout.CENTER);
        return panel;
    }

    // ---------- Shared helper ----------

    private Integer parseAppointmentNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid appointment number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}
