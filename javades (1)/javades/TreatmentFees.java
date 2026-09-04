import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the fixed consultation fee and the price list for each treatment type.
 * A LinkedHashMap is used so treatments always display in the same order
 * (important since the GUI combo box is built directly from this order).
 */
public class TreatmentFees {

    public static final double CONSULTATION_FEE = 500.0;

    private static final Map<String, Double> FEES = new LinkedHashMap<>();

    static {
        FEES.put("Consultation Only", 0.0);
        FEES.put("Scaling and Polishing", 2000.0);
        FEES.put("Tooth Filling", 3000.0);
        FEES.put("Tooth Extraction", 2500.0);
        FEES.put("Root Canal Treatment", 15000.0);
        FEES.put("Braces Consultation", 5000.0);
        FEES.put("Teeth Whitening", 8000.0);
    }

    public static Double getFee(String treatmentType) {
        return FEES.get(treatmentType);
    }

    /** Returns all treatment names, in display order, for populating a JComboBox. */
    public static String[] getTreatmentNames() {
        return FEES.keySet().toArray(new String[0]);
    }
}
