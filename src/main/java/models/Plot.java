
package models;

import java.io.Serial;
import java.io.Serializable;

public class Plot implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String type;
    private final boolean electricity;
    private final int size;
    private final int bedrooms;
    private final int beds;
    private final int maxGuests;
    private final double costPerNight;
    private final String facilities;
    private final String photoUrl;
    private boolean active = true;

    // Backwards-compatible constructor for existing code and saved data.
    public Plot(String id, String type, boolean electricity, int size, int bedrooms,
                int maxGuests, double costPerNight, String facilities, String photoUrl) {
        this(id, type, electricity, size, bedrooms, bedrooms, maxGuests,
                costPerNight, facilities, photoUrl);
    }

    public Plot(String id, String type, boolean electricity, int size, int bedrooms,
                int beds, int maxGuests, double costPerNight, String facilities, String photoUrl) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Plot ID is required.");
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Plot type is required.");
        }

        if (size < 1 || bedrooms < 0 || beds < 0 || maxGuests < 1 || costPerNight < 0) {
            throw new IllegalArgumentException(
                    "Invalid plot size, bedroom/bed count, guest capacity or cost."
            );
        }

        this.id = id.trim();
        this.type = type.trim();
        this.electricity = electricity;
        this.size = size;
        this.bedrooms = bedrooms;
        this.beds = beds;
        this.maxGuests = maxGuests;
        this.costPerNight = costPerNight;
        this.facilities = facilities == null ? "" : facilities.trim();
        this.photoUrl = photoUrl == null ? "" : photoUrl.trim();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean hasElectricity() {
        return electricity;
    }

    public int getSize() {
        return size;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBeds() {
        return beds;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public double getCostPerNight() {
        return costPerNight;
    }

    public String getFacilities() {
        return facilities;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Checks comma-separated facilities without creating an array.
    public boolean hasFacility(String facility) {
        if (facility == null || facility.isBlank()) {
            return true;
        }

        String requested = facility.trim();
        int start = 0;

        while (start < facilities.length()) {
            int comma = facilities.indexOf(',', start);

            int end;
            if (comma == -1) {
                end = facilities.length();
            } else {
                end = comma;
            }

            String supplied = facilities.substring(start, end).trim();

            if (supplied.equalsIgnoreCase(requested)) {
                return true;
            }

            if (comma == -1) {
                break;
            }

            start = comma + 1;
        }

        return false;
    }

    @Override
    public String toString() {
        return id + " (" + type + ", " + size + " m², "
                + bedrooms + " bedrooms, " + beds + " beds, €"
                + costPerNight + "/night, " + maxGuests + " guests)";
    }
}
