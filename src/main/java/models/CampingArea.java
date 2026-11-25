package models;

import DataStructures.CustomLinkedList;

import java.io.Serial;
import java.io.Serializable;

public class CampingArea implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String description;
    private final CustomLinkedList<Plot> plots = new CustomLinkedList<>();

    public CampingArea(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id; }
    public String getDescription() {
        return description; }
    public CustomLinkedList<Plot> getPlots() {
        return plots; }

    public Plot findPlot(String plotId) {
        for (Plot plot : plots) if (plot.getId().equalsIgnoreCase(plotId)) return plot;
        return null;
    }

    @Override
    public String toString() { return id + " - " + description; }
}
