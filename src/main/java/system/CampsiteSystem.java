package system;

import DataStructures.CustomLinkedList;
import models.*;

import java.time.LocalDate;

public class CampsiteSystem {
    private CustomLinkedList<Campsite> campsites = new CustomLinkedList<>();
    private CustomLinkedList<Guest> guests = new CustomLinkedList<>();
    private CustomLinkedList<Booking> bookings = new CustomLinkedList<>();
    private int nextBookingNumber = 1;

    public CustomLinkedList<Campsite> getCampsites() { return campsites; }
    public CustomLinkedList<Guest> getGuests() { return guests; }
    public CustomLinkedList<Booking> getBookings() { return bookings; }

    public Campsite addCampsite(String name, String address, int stars, String contact) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Campsite name is required.");
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Stars must be from 1 to 5.");
        if (findCampsite(name) != null) throw new IllegalArgumentException("That campsite already exists.");
        Campsite campsite = new Campsite(name.trim(), address == null ? "" : address.trim(), stars,
                contact == null ? "" : contact.trim());
        campsites.add(campsite);
        return campsite;
    }

    public Facility addCampsiteFacility(String campsiteName, String facilityName, String description) {
        Campsite campsite = requireCampsite(campsiteName);
        if (facilityName == null || facilityName.isBlank()) {
            throw new IllegalArgumentException("Facility name is required.");
        }
        for (Facility existing : campsite.getFacilities()) {
            if (existing.getName().equalsIgnoreCase(facilityName.trim())) {
                throw new IllegalArgumentException("That facility already exists at this campsite.");
            }
        }
        Facility facility = new Facility(facilityName.trim(), description == null ? "" : description.trim());
        campsite.getFacilities().add(facility);
        return facility;
    }

    public CampingArea addArea(String campsiteName, String areaId, String description) {
        Campsite campsite = requireCampsite(campsiteName);
        if (areaId == null || areaId.isBlank()) throw new IllegalArgumentException("Area ID is required.");
        if (campsite.findArea(areaId) != null) throw new IllegalArgumentException("Area ID already exists in this campsite.");
        CampingArea area = new CampingArea(areaId.trim(), description == null ? "" : description.trim());
        campsite.getAreas().add(area);
        return area;
    }

    // Original method retained so existing GUI code and tests continue to work.
    public Plot addPlot(String campsiteName, String areaId, String plotId, String type,
                        boolean electricity, int size, int bedrooms, int maxGuests,
                        double cost, String facilities, String photoUrl) {
        return addPlot(campsiteName, areaId, plotId, type, electricity, size,
                bedrooms, bedrooms, maxGuests, cost, facilities, photoUrl);
    }

    public Plot addPlot(String campsiteName, String areaId, String plotId, String type,
                        boolean electricity, int size, int bedrooms, int beds, int maxGuests,
                        double cost, String facilities, String photoUrl) {
        CampingArea area = requireArea(campsiteName, areaId);
        if (plotId == null || plotId.isBlank()) throw new IllegalArgumentException("Plot ID is required.");
        if (area.findPlot(plotId) != null) throw new IllegalArgumentException("Plot ID already exists in this area.");
        Plot plot = new Plot(plotId, type, electricity, size, bedrooms, beds,
                maxGuests, cost, facilities, photoUrl);
        area.getPlots().add(plot);
        return plot;
    }

    public Guest addGuest(String id, String name, LocalDate dob, String contact, String notes) {
        if (id == null || id.isBlank() || name == null || name.isBlank())
            throw new IllegalArgumentException("Guest ID and name are required.");
        if (findGuest(id) != null) throw new IllegalArgumentException("Guest ID already exists.");
        Guest guest = new Guest(id.trim(), name.trim(), dob, contact, notes);
        guests.add(guest);
        return guest;
    }

    public Booking createBooking(String guestId, String campsiteName, String areaId,
                                 LocalDate start, LocalDate end, BookingPreferences preferences) {
        Guest guest = requireGuest(guestId);
        Campsite campsite = requireCampsite(campsiteName);
        CampingArea area = requireArea(campsiteName, areaId);
        validateDates(start, end);
        if (preferences == null) throw new IllegalArgumentException("Booking preferences are required.");

        Plot chosen = null;
        for (Plot plot : area.getPlots()) {
            if (plot.isActive() && matches(plot, preferences) && isAvailable(plot, start, end)) {
                chosen = plot;
                break;
            }
        }
        if (chosen == null) throw new IllegalStateException("No available plot in that area matches all preferences.");

        Booking booking = new Booking("B" + nextBookingNumber++, guest, campsite, area, chosen,
                start, end, preferences);
        bookings.add(booking);
        guest.getBookings().add(booking);
        return booking;
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("End date must be after start date; both dates are required.");
        }
    }

    private boolean matches(Plot plot, BookingPreferences preferences) {
        boolean typeMatches = preferences.getPreferredType().equalsIgnoreCase("Any")
                || plot.getType().equalsIgnoreCase(preferences.getPreferredType());
        return typeMatches
                && plot.getMaxGuests() >= preferences.getGuests()
                && (!preferences.isElectricityRequired() || plot.hasElectricity())
                && plot.getSize() >= preferences.getMinimumSize()
                && plot.getBedrooms() >= preferences.getMinimumBedrooms()
                && plot.getCostPerNight() <= preferences.getMaximumBudget();
    }

    private boolean isAvailable(Plot plot, LocalDate start, LocalDate end) {
        for (Booking booking : bookings) {
            if (booking.getPlot() == plot && booking.overlaps(start, end)) return false;
        }
        return true;
    }

    public boolean cancelBooking(String bookingId) {
        if (bookingId == null) return false;
        for (Booking booking : bookings) {
            if (booking.getId().equalsIgnoreCase(bookingId.trim()) && !booking.isCancelled()) {
                booking.cancel();
                return true;
            }
        }
        return false;
    }

    // Existing Smart Find signature retained for the existing GUI and tests.
    public CustomLinkedList<Plot> smartFind(int minimumStars, String requiredFacility,
                                            LocalDate start, LocalDate end, int guests,
                                            boolean electricity, double maxBudget) {
        return smartFind(minimumStars, "Any", requiredFacility, null, 0, 0,
                start, end, guests, electricity, maxBudget);
    }

    // Expanded Smart Find: campsite facility, plot type/facility, size and bedrooms.
    public CustomLinkedList<Plot> smartFind(int minimumStars, String preferredType,
                                            String requiredPlotFacility, String requiredCampsiteFacility,
                                            int minimumSize, int minimumBedrooms, LocalDate start, LocalDate end,
                                            int guests, boolean electricity, double maxBudget) {
        if (minimumStars < 1 || minimumStars > 5) throw new IllegalArgumentException("Minimum stars must be from 1 to 5.");
        if (guests < 1) throw new IllegalArgumentException("Guest count must be at least 1.");
        if (minimumSize < 0 || minimumBedrooms < 0) throw new IllegalArgumentException("Minimum size and bedrooms cannot be negative.");
        if (maxBudget < 0) throw new IllegalArgumentException("Budget cannot be negative.");
        if ((start == null) != (end == null)) throw new IllegalArgumentException("Enter both dates or leave both dates blank.");
        if (start != null) validateDates(start, end);

        CustomLinkedList<Plot> results = new CustomLinkedList<>();
        for (Campsite campsite : campsites) {
            if (campsite.getStars() < minimumStars || !campsiteHasFacility(campsite, requiredCampsiteFacility)) continue;
            for (CampingArea area : campsite.getAreas()) {
                for (Plot plot : area.getPlots()) {
                    boolean typeMatches = preferredType == null || preferredType.isBlank()
                            || preferredType.equalsIgnoreCase("Any") || plot.getType().equalsIgnoreCase(preferredType);
                    boolean datesAvailable = start == null || isAvailable(plot, start, end);
                    if (plot.isActive() && typeMatches && plot.getMaxGuests() >= guests
                            && (!electricity || plot.hasElectricity())
                            && plot.getSize() >= minimumSize
                            && plot.getBedrooms() >= minimumBedrooms
                            && plot.getCostPerNight() <= maxBudget
                            && plot.hasFacility(requiredPlotFacility)
                            && datesAvailable) {
                        results.add(plot);
                    }
                }
            }
        }
        return results;
    }

    private boolean campsiteHasFacility(Campsite campsite, String requiredFacility) {
        if (requiredFacility == null || requiredFacility.isBlank()) return true;
        for (Facility facility : campsite.getFacilities()) {
            if (facility.getName().equalsIgnoreCase(requiredFacility.trim())) return true;
        }
        return false;
    }

    public boolean removePlot(String campsiteName, String areaId, String plotId) {
        Campsite campsite = requireCampsite(campsiteName);
        CampingArea originalArea = requireArea(campsiteName, areaId);
        Plot target = originalArea.findPlot(plotId);
        if (target == null) return false;

        // First find every replacement without changing any booking. This prevents
        // a partly completed removal if one booking cannot be reassigned.
        CustomLinkedList<Booking> affectedBookings = new CustomLinkedList<>();
        CustomLinkedList<CampingArea> replacementAreas = new CustomLinkedList<>();
        CustomLinkedList<Plot> replacementPlots = new CustomLinkedList<>();

        for (Booking booking : bookings) {
            if (!booking.isCancelled() && booking.getPlot() == target) {
                Replacement replacement = findReplacement(campsite, booking, target);
                if (replacement == null) {
                    throw new IllegalStateException("Cannot remove plot: booking " + booking.getId()
                            + " cannot be reassigned anywhere in campsite " + campsite.getName() + ".");
                }
                affectedBookings.add(booking);
                replacementAreas.add(replacement.area);
                replacementPlots.add(replacement.plot);
            }
        }

        for (int i = 0; i < affectedBookings.size(); i++) {
            affectedBookings.get(i).reassign(replacementAreas.get(i), replacementPlots.get(i));
        }

        target.setActive(false);
        originalArea.getPlots().removeItem(target);
        return true;
    }

    private Replacement findReplacement(Campsite campsite, Booking booking, Plot excluded) {
        for (CampingArea area : campsite.getAreas()) {
            for (Plot candidate : area.getPlots()) {
                if (candidate != excluded && candidate.isActive()
                        && matches(candidate, booking.getPreferences())
                        && isAvailableForOtherBookings(candidate, booking)) {
                    return new Replacement(area, candidate);
                }
            }
        }
        return null;
    }

    private boolean isAvailableForOtherBookings(Plot plot, Booking ignored) {
        for (Booking other : bookings) {
            if (other != ignored && other.getPlot() == plot
                    && other.overlaps(ignored.getStartDate(), ignored.getEndDate())) return false;
        }
        return true;
    }

    private static class Replacement {
        private final CampingArea area;
        private final Plot plot;

        private Replacement(CampingArea area, Plot plot) {
            this.area = area;
            this.plot = plot;
        }
    }

    public Campsite findCampsite(String name) {
        if (name == null) return null;
        for (Campsite campsite : campsites) {
            if (campsite.getName().equalsIgnoreCase(name.trim())) return campsite;
        }
        return null;
    }

    public Guest findGuest(String id) {
        if (id == null) return null;
        for (Guest guest : guests) {
            if (guest.getId().equalsIgnoreCase(id.trim())) return guest;
        }
        return null;
    }

    private Campsite requireCampsite(String name) {
        Campsite campsite = findCampsite(name);
        if (campsite == null) throw new IllegalArgumentException("Campsite not found: " + name);
        return campsite;
    }

    private Guest requireGuest(String id) {
        Guest guest = findGuest(id);
        if (guest == null) throw new IllegalArgumentException("Guest not found: " + id);
        return guest;
    }

    private CampingArea requireArea(String campsiteName, String areaId) {
        CampingArea area = requireCampsite(campsiteName).findArea(areaId);
        if (area == null) throw new IllegalArgumentException("Area not found: " + areaId);
        return area;
    }

    public void reset() {
        campsites.clear();
        guests.clear();
        bookings.clear();
        nextBookingNumber = 1;
    }

    public void restoreData(CustomLinkedList<Campsite> loadedCampsites,
                            CustomLinkedList<Guest> loadedGuests,
                            CustomLinkedList<Booking> loadedBookings, int nextNumber) {
        if (loadedCampsites == null || loadedGuests == null || loadedBookings == null) {
            throw new IllegalArgumentException("Saved data is incomplete.");
        }
        campsites = loadedCampsites;
        guests = loadedGuests;
        bookings = loadedBookings;
        nextBookingNumber = Math.max(1, nextNumber);
    }

    public int getNextBookingNumber() { return nextBookingNumber; }
}
