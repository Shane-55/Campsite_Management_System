package models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final Guest guest;
    private final Campsite campsite;
    private CampingArea area;
    private Plot plot;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BookingPreferences preferences;
    private boolean cancelled;

    public Booking(String id, Guest guest, Campsite campsite, CampingArea area, Plot plot,
                   LocalDate startDate, LocalDate endDate, BookingPreferences preferences) {
        this.id = id;
        this.guest = guest;
        this.campsite = campsite;
        this.area = area;
        this.plot = plot;
        this.startDate = startDate;
        this.endDate = endDate;
        this.preferences = preferences;
        this.cancelled = false;
    }

    public String getId() { return id; }
    public Guest getGuest() { return guest; }
    public Campsite getCampsite() { return campsite; }
    public CampingArea getArea() { return area; }
    public Plot getPlot() { return plot; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BookingPreferences getPreferences() { return preferences; }
    public boolean isCancelled() { return cancelled; }

    public void setPlot(Plot plot) {
        if (plot == null) throw new IllegalArgumentException("Replacement plot cannot be null.");
        this.plot = plot;
    }

    public void setArea(CampingArea area) {
        if (area == null) throw new IllegalArgumentException("Replacement area cannot be null.");
        this.area = area;
    }

    public void reassign(CampingArea newArea, Plot newPlot) {
        setArea(newArea);
        setPlot(newPlot);
    }

    public void cancel() { cancelled = true; }

    public long getNights() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    // End dates are checkout dates, so a booking ending on another booking's
    // start date does not overlap it.
    public boolean overlaps(LocalDate start, LocalDate end) {
        return !cancelled && startDate.isBefore(end) && endDate.isAfter(start);
    }

    @Override
    public String toString() {
        return id + " | " + guest.getName() + " | " + campsite.getName() + " | "
                + area.getId() + "/" + plot.getId() + " | " + startDate + " to " + endDate
                + (cancelled ? " | CANCELLED" : "");
    }
}
