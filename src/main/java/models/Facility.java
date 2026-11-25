package models;

import java.io.Serial;
import java.io.Serializable;

public class Facility implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String description;

    public Facility(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name; }
    public String getDescription() {
        return description; }

    @Override
    public String toString() {
        return name + (description == null || description.isBlank() ? "" : " - " + description);
    }
}
