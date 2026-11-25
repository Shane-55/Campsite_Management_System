package persistence;

import DataStructures.CustomLinkedList;
import models.*;
import system.CampsiteSystem;

import java.io.*;

public class FileManager {
    private static class SavedData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        CustomLinkedList<Campsite> campsites;
        CustomLinkedList<Guest> guests;
        CustomLinkedList<Booking> bookings;
        int nextBookingNumber;

        SavedData(CampsiteSystem system) {
            campsites = system.getCampsites();
            guests = system.getGuests();
            bookings = system.getBookings();
            nextBookingNumber = system.getNextBookingNumber();
        }
    }

    public static void save(CampsiteSystem system, File file) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(new SavedData(system));
        }
    }

    public static void load(CampsiteSystem system, File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            SavedData data = (SavedData) input.readObject();
            system.restoreData(data.campsites, data.guests, data.bookings, data.nextBookingNumber);
        }
    }
}
