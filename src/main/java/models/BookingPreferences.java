package models;

import java.io.Serial;
import java.io.Serializable;

public class BookingPreferences implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String preferredType;
    private final int guests;
    private final boolean electricityRequired;
    private final int minimumSize;
    private final int minimumBedrooms;
    private final double maximumBudget;

    public BookingPreferences(String preferredType, int guests, boolean electricityRequired,
                              int minimumSize, int minimumBedrooms, double maximumBudget) {
        this.preferredType = preferredType == null ? "Any" : preferredType;
        this.guests = guests;
        this.electricityRequired = electricityRequired;
        this.minimumSize = minimumSize;
        this.minimumBedrooms = minimumBedrooms;
        this.maximumBudget = maximumBudget;
    }

    public String getPreferredType() {
        return preferredType; }
    public int getGuests() {
        return guests; }
    public boolean isElectricityRequired() {
        return electricityRequired; }
    public int getMinimumSize() {
        return minimumSize; }
    public int getMinimumBedrooms() {
        return minimumBedrooms; }
    public double getMaximumBudget() {
        return maximumBudget; }
}
