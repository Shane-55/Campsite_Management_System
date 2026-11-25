package models;

import DataStructures.CustomLinkedList;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Guest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final LocalDate dateOfBirth;
    private final String contactInfo;
    private final String notes;
    private final CustomLinkedList<Booking> bookings = new CustomLinkedList<>();

    public Guest(String id, String name, LocalDate dateOfBirth, String contactInfo, String notes) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.contactInfo = contactInfo;
        this.notes = notes == null ? "" : notes;
    }

    public String getId() {
        return id; }
    public String getName() {
        return name; }
    public LocalDate getDateOfBirth() {
        return dateOfBirth; }
    public String getContactInfo() {
        return contactInfo; }
    public String getNotes() {
        return notes; }
    public CustomLinkedList<Booking> getBookings() {
        return bookings; }

    @Override
    public String toString() { return id + " - " + name; }
}
