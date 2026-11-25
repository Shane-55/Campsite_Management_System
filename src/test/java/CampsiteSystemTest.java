import models.*;
import system.CampsiteSystem;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CampsiteSystemTest {

    @Test
    void canAddCampsiteAreaAndPlot() {
        CampsiteSystem system = new CampsiteSystem();
        system.addCampsite("Lakeview", "Waterford", 4, "info@example.com");
        system.addArea("Lakeview", "A1", "Near lake");
        Plot plot = system.addPlot("Lakeview", "A1", "P1", "Pitch", true,
                50, 0, 4, 25, "Toilets, showers", "");
        assertEquals("P1", plot.getId());
        assertEquals(1, system.getCampsites().size());
    }

    @Test
    void createsBookingAndAssignsAvailablePlot() {
        CampsiteSystem system = sampleSystem();
        Booking booking = system.createBooking("G1", "Lakeview", "A1",
                LocalDate.of(2027, 6, 1), LocalDate.of(2027, 6, 4), standardPreferences());
        assertEquals("P1", booking.getPlot().getId());
        assertEquals(1, system.getBookings().size());
    }

    @Test
    void doesNotAllowOverlappingBookingsOnSamePlot() {
        CampsiteSystem system = sampleSystem();
        system.createBooking("G1", "Lakeview", "A1", LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 4), standardPreferences());
        assertThrows(IllegalStateException.class, () -> system.createBooking("G1", "Lakeview", "A1",
                LocalDate.of(2027, 6, 2), LocalDate.of(2027, 6, 5), standardPreferences()));
    }

    @Test
    void allowsBookingsOnAdjacentDates() {
        CampsiteSystem system = sampleSystem();
        system.createBooking("G1", "Lakeview", "A1", LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 4), standardPreferences());
        Booking second = system.createBooking("G1", "Lakeview", "A1", LocalDate.of(2027, 6, 4),
                LocalDate.of(2027, 6, 7), standardPreferences());
        assertNotNull(second);
        assertEquals(2, system.getBookings().size());
    }

    @Test
    void canCancelBooking() {
        CampsiteSystem system = sampleSystem();
        Booking booking = system.createBooking("G1", "Lakeview", "A1", LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 4), standardPreferences());
        assertTrue(system.cancelBooking(booking.getId()));
        assertTrue(booking.isCancelled());
    }

    @Test
    void cancelledBookingMakesPlotAvailableAgain() {
        CampsiteSystem system = sampleSystem();
        Booking first = system.createBooking("G1", "Lakeview", "A1", LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 4), standardPreferences());
        system.cancelBooking(first.getId());
        Booking second = system.createBooking("G1", "Lakeview", "A1", LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 4), standardPreferences());
        assertNotNull(second);
        assertEquals("P1", second.getPlot().getId());
    }

    @Test
    void doesNotAllowDuplicateAreaIdentifiersInSameCampsite() {
        CampsiteSystem system = sampleSystem();
        assertThrows(IllegalArgumentException.class,
                () -> system.addArea("Lakeview", "A1", "Another area"));
    }

    @Test
    void rejectsInvalidBookingDates() {
        CampsiteSystem system = sampleSystem();
        assertThrows(IllegalArgumentException.class, () -> system.createBooking("G1", "Lakeview", "A1",
                LocalDate.of(2027, 6, 5), LocalDate.of(2027, 6, 1), standardPreferences()));
    }

    @Test
    void smartFindReturnsSuitablePlots() {
        CampsiteSystem system = sampleSystem();
        var results = system.smartFind(4, "Showers", LocalDate.of(2027, 7, 1),
                LocalDate.of(2027, 7, 5), 2, true, 40);
        assertEquals(1, results.size());
        assertEquals("P1", results.get(0).getId());
    }

    @Test
    void canAddCampsiteFacilityAndUseItInSmartFind() {
        CampsiteSystem system = sampleSystem();
        system.addCampsiteFacility("Lakeview", "Swimming pool", "Outdoor heated pool");
        var results = system.smartFind(4, "Pitch", "Showers", "Swimming pool", 20, 0,
                LocalDate.of(2027, 7, 1), LocalDate.of(2027, 7, 5), 2, true, 40);
        assertEquals(1, results.size());
    }

    @Test
    void storesSeparateBedCountForPlot() {
        CampsiteSystem system = sampleSystem();
        Plot staticPlot = system.addPlot("Lakeview", "A1", "S1", "Static", true,
                80, 2, 4, 4, 25, "Heating", "");
        assertEquals(2, staticPlot.getBedrooms());
        assertEquals(4, staticPlot.getBeds());
    }

    @Test
    void removingPlotCanReassignBookingToAnotherAreaInSameCampsite() {
        CampsiteSystem system = sampleSystem();
        system.addArea("Lakeview", "A2", "Second area");
        system.addPlot("Lakeview", "A2", "P2", "Pitch", true,
                50, 0, 4, 25, "Toilets, Showers", "");

        Booking booking = system.createBooking("G1", "Lakeview", "A1",
                LocalDate.of(2027, 8, 1), LocalDate.of(2027, 8, 5), standardPreferences());

        assertTrue(system.removePlot("Lakeview", "A1", "P1"));
        assertEquals("A2", booking.getArea().getId());
        assertEquals("P2", booking.getPlot().getId());
        assertEquals(0, system.findCampsite("Lakeview").findArea("A1").getPlots().size());
    }

    private CampsiteSystem sampleSystem() {
        CampsiteSystem system = new CampsiteSystem();
        system.addCampsite("Lakeview", "Waterford", 4, "info@example.com");
        system.addArea("Lakeview", "A1", "Near lake");
        system.addPlot("Lakeview", "A1", "P1", "Pitch", true,
                50, 0, 4, 25, "Toilets, Showers", "");
        system.addGuest("G1", "Alex Smith", LocalDate.of(2000, 1, 1), "alex@example.com", "");
        return system;
    }

    private BookingPreferences standardPreferences() {
        return new BookingPreferences("Pitch", 2, true, 20, 0, 40);
    }
}
