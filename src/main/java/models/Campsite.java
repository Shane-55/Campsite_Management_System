package models;

import DataStructures.CustomLinkedList;

import java.io.Serial;
import java.io.Serializable;

public class Campsite implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String address;
    private final int stars;
    private final String contactInfo;
    private final CustomLinkedList<Facility> facilities = new CustomLinkedList<>();
    private final CustomLinkedList<CampingArea> areas = new CustomLinkedList<>();

    public Campsite(String name, String address, int stars, String contactInfo) {
        this.name = name;
        this.address = address;
        this.stars = stars;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name; }
    public String getAddress() {
        return address; }
    public int getStars() {
        return stars; }
    public String getContactInfo() {
        return contactInfo; }
    public CustomLinkedList<Facility> getFacilities() {
        return facilities; }
    public CustomLinkedList<CampingArea> getAreas() {
        return areas; }

    public CampingArea findArea(String areaId) {
        for (CampingArea area : areas) if (area.getId().equalsIgnoreCase(areaId)) return area;
        return null;
    }

    @Override
    public String toString() { return name + " (" + stars + " stars)"; }
}
