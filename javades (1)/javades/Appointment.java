/**
 * Represents a single patient appointment record.
 */
public class Appointment {
    private int appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private String treatmentType;
    private String appointmentDate;
    private String appointmentTime;

    public Appointment(int appointmentNumber, String patientName, String address,
                        String contactNumber, String dentistName, String treatmentType,
                        String appointmentDate, String appointmentTime) {
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    public int getAppointmentNumber() { return appointmentNumber; }
    public String getPatientName() { return patientName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getDentistName() { return dentistName; }
    public String getTreatmentType() { return treatmentType; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }

    /** Multi-line, human-readable details block used by the Display and Bill panels. */
    public String toDetailsText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Appointment Number  : ").append(appointmentNumber).append("\n");
        sb.append("Patient Name        : ").append(patientName).append("\n");
        sb.append("Address             : ").append(address).append("\n");
        sb.append("Contact Number      : ").append(contactNumber).append("\n");
        sb.append("Dentist Name        : ").append(dentistName).append("\n");
        sb.append("Treatment Type      : ").append(treatmentType).append("\n");
        sb.append("Appointment Date    : ").append(appointmentDate).append("\n");
        sb.append("Appointment Time    : ").append(appointmentTime).append("\n");
        return sb.toString();
    }
}
