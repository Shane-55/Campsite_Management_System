
package GUI;

import models.*;
import persistence.FileManager;
import system.CampsiteSystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainWindow extends Application {

    private final CampsiteSystem system = new CampsiteSystem();
    private TextArea output;

    @Override
    public void start(Stage stage) {

        stage.setTitle("Campsite Management System");

        output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        TabPane tabs = new TabPane();

        tabs.getTabs().add(makeTab("Campsites", campsiteForm()));
        tabs.getTabs().add(makeTab("Areas and plots", plotForm()));
        tabs.getTabs().add(makeTab("Guests", guestForm()));
        tabs.getTabs().add(makeTab("Bookings", bookingForm()));
        tabs.getTabs().add(makeTab("Smart find", smartFindForm()));

        VBox root = getvBox(stage, tabs);

        VBox.setVgrow(output, Priority.ALWAYS);

        stage.setScene(new Scene(root, 950, 750));
        stage.show();

        log("Welcome. Add a campsite, its facilities, areas and plots, "
                + "then add guests and bookings.");
    }

    private VBox getvBox(Stage stage, TabPane tabs) {

        Button view = new Button("Show all data");
        view.setOnAction(_ -> showAll());

        Button save = new Button("Save data");
        save.setOnAction(_ -> save(stage));

        Button load = new Button("Load data");
        load.setOnAction(_ -> load(stage));

        Button reset = new Button("Reset system");
        reset.setOnAction(_ -> {
            if (confirm()) {
                system.reset();
                log("System reset.");
            }
        });

        HBox actions = new HBox(10, view, save, load, reset);

        VBox root = new VBox(
                10,
                tabs,
                new Label("System output"),
                output,
                actions
        );

        root.setPadding(new Insets(12));

        return root;
    }

    private Tab makeTab(String name, VBox content) {

        Tab tab = new Tab(name);

        tab.setClosable(false);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);

        tab.setContent(scroll);

        return tab;
    }

    private VBox form() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        return box;
    }

    private TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private CheckBox check(String label) {
        return new CheckBox(label);
    }

    private Button action(String label, Runnable task) {

        Button button = new Button(label);

        button.setOnAction(_ -> runSafely(task));

        return button;
    }

    private VBox campsiteForm() {

        VBox box = form();

        TextField name = field("Campsite name");
        TextField address = field("Address");
        TextField stars = field("Star rating (1-5)");
        TextField contact = field("Contact information");

        box.getChildren().addAll(
                new Label("Add a campsite"),
                name,
                address,
                stars,
                contact,
                action("Add campsite", () -> {

                    Campsite campsite = system.addCampsite(
                            name.getText(),
                            address.getText(),
                            Integer.parseInt(stars.getText()),
                            contact.getText()
                    );

                    log("Added campsite: " + campsite);

                    name.clear();
                    address.clear();
                    stars.clear();
                    contact.clear();
                })
        );

        TextField facilityCampsite =
                field("Campsite name for facility");

        TextField facilityName =
                field("Facility name (e.g. Swimming pool)");

        TextField facilityDescription =
                field("Facility description (optional)");

        box.getChildren().addAll(
                new Separator(),
                new Label("Add a campsite facility"),
                facilityCampsite,
                facilityName,
                facilityDescription,
                action("Add campsite facility", () -> {

                    Facility facility = system.addCampsiteFacility(
                            facilityCampsite.getText(),
                            facilityName.getText(),
                            facilityDescription.getText()
                    );

                    log("Added facility to "
                            + facilityCampsite.getText()
                            + ": " + facility);

                    facilityName.clear();
                    facilityDescription.clear();
                })
        );

        return box;
    }

    private VBox plotForm() {

        VBox box = form();

        TextField campsite = field("Campsite name");
        TextField areaId = field("Area ID");
        TextField description = field("Area description");

        Button addArea = action("Add area", () -> {

            CampingArea area = system.addArea(
                    campsite.getText(),
                    areaId.getText(),
                    description.getText()
            );

            log("Added area: " + area);
        });

        TextField plotId = field("Plot ID");

        TextField type = field(
                "Type: Pitch, Static, Bungalow, Apartment, etc."
        );

        CheckBox power = check("Electricity available");

        TextField size = field("Size in square metres");

        TextField bedrooms =
                field("Bedrooms (0 for empty pitch)");

        TextField beds =
                field("Number of beds (0 for empty pitch)");

        TextField capacity = field("Maximum guests");

        TextField cost = field("Cost per night (€)");

        TextField facilities =
                field("Plot facilities (comma separated)");

        TextField photo = field("Photo URL (optional)");

        Button addPlot = action("Add plot", () -> {

            Plot plot = system.addPlot(
                    campsite.getText(),
                    areaId.getText(),
                    plotId.getText(),
                    type.getText(),
                    power.isSelected(),
                    Integer.parseInt(size.getText()),
                    Integer.parseInt(bedrooms.getText()),
                    Integer.parseInt(beds.getText()),
                    Integer.parseInt(capacity.getText()),
                    Double.parseDouble(cost.getText()),
                    facilities.getText(),
                    photo.getText()
            );

            log("Added plot: " + plot);

            plotId.clear();
            type.clear();
            size.clear();
            bedrooms.clear();
            beds.clear();
            capacity.clear();
            cost.clear();
            facilities.clear();
            photo.clear();
            power.setSelected(false);
        });

        TextField removeId = field("Plot ID to remove");

        Button remove = action(
                "Remove plot and reassign bookings",
                () -> {

                    boolean removed = system.removePlot(
                            campsite.getText(),
                            areaId.getText(),
                            removeId.getText()
                    );

                    log(removed
                            ? "Plot removed. Pending bookings were reassigned within the campsite."
                            : "Plot not found.");
                }
        );

        box.getChildren().addAll(
                new Label("Add area"),
                campsite,
                areaId,
                description,
                addArea,
                new Separator(),
                new Label("Add plot to the selected campsite and area"),
                plotId,
                type,
                power,
                size,
                bedrooms,
                beds,
                capacity,
                cost,
                facilities,
                photo,
                addPlot,
                new Separator(),
                new Label("Remove plot"),
                removeId,
                remove
        );

        return box;
    }

    private VBox guestForm() {

        VBox box = form();

        TextField id = field("Guest ID");
        TextField name = field("Guest name");

        DatePicker dob = new DatePicker();

        TextField contact = field("Phone or email");
        TextField notes = field("Additional notes");

        Button add = action("Add guest", () -> {

            Guest guest = system.addGuest(
                    id.getText(),
                    name.getText(),
                    dob.getValue(),
                    contact.getText(),
                    notes.getText()
            );

            log("Added guest: " + guest);

            id.clear();
            name.clear();
            contact.clear();
            notes.clear();
            dob.setValue(null);
        });

        TextField lookup = field("Guest ID to view");

        Button view = action("View guest record", () -> {

            Guest guest = system.findGuest(lookup.getText());

            if (guest == null) {

                log("Guest not found.");

            } else {

                log("Guest: " + guest.getName()
                        + " | DOB: " + guest.getDateOfBirth()
                        + " | Contact: " + guest.getContactInfo()
                        + " | Notes: " + guest.getNotes());

                for (Booking booking : guest.getBookings()) {

                    String status;

                    if (booking.isCancelled()) {
                        status = "CANCELLED";
                    } else if (booking.getEndDate()
                            .isBefore(java.time.LocalDate.now())) {
                        status = "PAST";
                    } else {
                        status = "PENDING/UPCOMING";
                    }

                    log("  [" + status + "] " + booking);
                }
            }
        });

        box.getChildren().addAll(
                new Label("Add guest"),
                id,
                name,
                new Label("Date of birth"),
                dob,
                contact,
                notes,
                add,
                new Separator(),
                lookup,
                view
        );

        return box;
    }

    private VBox bookingForm() {

        VBox box = form();

        TextField guestId = field("Guest ID");
        TextField campsite = field("Campsite name");
        TextField area = field("Area ID");

        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();

        TextField type = field(
                "Preferred type (Any, Pitch, Static, etc.)"
        );

        TextField guests = field("Number of guests");

        CheckBox power = check("Electricity required");

        TextField size = field("Minimum size (m²)");
        TextField bedrooms = field("Minimum bedrooms");

        TextField budget =
                field("Maximum cost per night (€)");

        Button create = action(
                "Find plot and create booking",
                () -> {

                    BookingPreferences preferences =
                            new BookingPreferences(
                                    type.getText().isBlank()
                                            ? "Any" : type.getText(),
                                    Integer.parseInt(guests.getText()),
                                    power.isSelected(),
                                    Integer.parseInt(size.getText()),
                                    Integer.parseInt(bedrooms.getText()),
                                    Double.parseDouble(budget.getText())
                            );

                    Booking booking = system.createBooking(
                            guestId.getText(),
                            campsite.getText(),
                            area.getText(),
                            start.getValue(),
                            end.getValue(),
                            preferences
                    );

                    log("Booking created and plot assigned: " + booking);
                }
        );

        TextField cancelId =
                field("Booking ID to cancel (e.g. B1)");

        Button cancel = action("Cancel booking", () ->
                log(system.cancelBooking(cancelId.getText())
                        ? "Booking cancelled."
                        : "Active booking not found.")
        );

        Button listArea = action(
                "Show pending bookings for campsite",
                () -> showCampsitePending(campsite.getText())
        );

        Button listAll = action(
                "Show all pending bookings",
                this::showAllPending
        );

        box.getChildren().addAll(
                new Label("Create booking"),
                guestId,
                campsite,
                area,
                new Label("Start date"),
                start,
                new Label("End date"),
                end,
                type,
                guests,
                power,
                size,
                bedrooms,
                budget,
                create,
                new Separator(),
                cancelId,
                cancel,
                listArea,
                listAll
        );

        return box;
    }

    private VBox smartFindForm() {

        VBox box = form();

        TextField stars =
                field("Minimum campsite stars (1-5)");

        TextField type =
                field("Preferred plot type (Any, Pitch, Static)");

        TextField campsiteFacility =
                field("Required campsite facility (optional)");

        TextField plotFacility =
                field("Required plot facility (optional)");

        TextField size =
                field("Minimum plot size (m²)");

        TextField bedrooms =
                field("Minimum bedrooms (0 if not applicable)");

        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();

        TextField guests = field("Number of guests");

        CheckBox power = check("Must have electricity");

        TextField budget =
                field("Maximum cost per night (€)");

        Button search = action("Search all campsites", () -> {

            var results = system.smartFind(
                    Integer.parseInt(stars.getText()),
                    type.getText(),
                    plotFacility.getText(),
                    campsiteFacility.getText(),
                    Integer.parseInt(size.getText()),
                    Integer.parseInt(bedrooms.getText()),
                    start.getValue(),
                    end.getValue(),
                    Integer.parseInt(guests.getText()),
                    power.isSelected(),
                    Double.parseDouble(budget.getText())
            );

            log("Smart find results: " + results.size());

            for (Campsite campsite : system.getCampsites()) {

                for (CampingArea area : campsite.getAreas()) {

                    for (Plot plot : results) {

                        if (area.getPlots().contains(plot)) {

                            log(campsite.getName()
                                    + " (" + campsite.getStars() + " stars)"
                                    + " / " + area.getId()
                                    + " / " + plot);
                        }
                    }
                }
            }
        });

        box.getChildren().addAll(
                new Label("Smart find suitable camping holidays"),
                stars,
                type,
                campsiteFacility,
                plotFacility,
                size,
                bedrooms,
                new Label("Start date (leave blank with end date)"),
                start,
                new Label("End date"),
                end,
                guests,
                power,
                budget,
                search
        );

        return box;
    }

    private boolean isPending(Booking booking) {
        return !booking.isCancelled()
                && !booking.getEndDate().isBefore(java.time.LocalDate.now());
    }

    private void showCampsitePending(String campsiteName) {

        Campsite campsite = system.findCampsite(campsiteName);

        if (campsite == null) {
            log("Campsite not found.");
            return;
        }

        int total = 0;

        log("===== PENDING BOOKINGS: "
                + campsite.getName() + " =====");

        for (CampingArea area : campsite.getAreas()) {

            log("Area: " + area.getId());

            for (Plot plot : area.getPlots()) {

                int plotCount = 0;

                for (Booking booking : system.getBookings()) {

                    if (isPending(booking) && booking.getPlot() == plot)  {

                        log("  Plot " + plot.getId()
                                + ": " + booking.getId()
                                + " | " + booking.getGuest().getName()
                                + " | " + booking.getStartDate()
                                + " to " + booking.getEndDate());

                        plotCount++;
                        total++;
                    }
                }

                if (plotCount == 0) {
                    log("  Plot " + plot.getId()
                            + ": no pending bookings");
                }
            }
        }

        log("Total pending bookings at "
                + campsite.getName() + ": " + total);
    }

    private void showAllPending() {

        int total = 0;

        log("===== ALL PENDING BOOKINGS =====");

        for (Campsite campsite : system.getCampsites()) {

            log("Campsite: " + campsite.getName());

            for (CampingArea area : campsite.getAreas()) {

                log("  Area: " + area.getId());

                for (Plot plot : area.getPlots()) {

                    for (Booking booking : system.getBookings()) {

                        if (!booking.isCancelled()
                                && booking.getPlot() == plot) {

                            log("    Plot " + plot.getId()
                                    + ": " + booking.getId()
                                    + " | " + booking.getGuest().getName()
                                    + " | " + booking.getStartDate()
                                    + " to " + booking.getEndDate());

                            total++;
                        }
                    }
                }
            }
        }

        log("Total pending bookings in system: " + total);
    }

    private void showAll() {

        log("===== CAMPSITES =====");

        for (Campsite campsite : system.getCampsites()) {

            log(campsite.toString()
                    + " | " + campsite.getAddress()
                    + " | " + campsite.getContactInfo());

            for (Facility facility : campsite.getFacilities()) {
                log("  Facility: " + facility);
            }

            for (CampingArea area : campsite.getAreas()) {

                log("  Area " + area);

                for (Plot plot : area.getPlots()) {
                    log("    " + plot
                            + " | Facilities: " + plot.getFacilities());
                }
            }
        }

        log("===== GUESTS =====");

        for (Guest guest : system.getGuests()) {
            log(guest.toString());
        }

        log("===== BOOKINGS =====");

        for (Booking booking : system.getBookings()) {
            log(booking.toString());
        }
    }

    private void save(Stage stage) {

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Save campsite data");
        chooser.setInitialFileName("campsite-data.dat");

        File file = chooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        try {
            FileManager.save(system, file);
            log("Saved data to " + file.getAbsolutePath());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void load(Stage stage) {

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Load campsite data");

        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        try {
            FileManager.load(system, file);
            log("Loaded data from " + file.getAbsolutePath());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void runSafely(Runnable task) {

        try {
            task.run();
        } catch (Exception ex) {
            showError(
                    ex.getMessage() == null
                            ? ex.toString()
                            : ex.getMessage()
            );
        }
    }

    private boolean confirm() {

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Reset all campsites, guests and bookings?",
                ButtonType.YES,
                ButtonType.NO
        );

        return alert.showAndWait()
                .orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showError(String message) {
        new Alert(
                Alert.AlertType.ERROR,
                message,
                ButtonType.OK
        ).showAndWait();
    }

    private void log(String message) {
        output.appendText(message + System.lineSeparator());
    }
}
