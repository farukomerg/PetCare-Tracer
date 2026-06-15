package com.petcareadmin;

import com.petcareadmin.client.BackendApiClient;
import com.petcareadmin.model.AppointmentItem;
import com.petcareadmin.model.PetItem;
import com.petcareadmin.model.ReminderItem;
import com.petcareadmin.model.UserItem;
import com.petcareadmin.ui.StatusBadgeCell;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PetCareAdminApplication extends Application {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final BackendApiClient apiClient = new BackendApiClient("http://localhost:8080");

    private final ObservableList<UserItem> users = FXCollections.observableArrayList();
    private final ObservableList<PetItem> pets = FXCollections.observableArrayList();
    private final ObservableList<AppointmentItem> appointments = FXCollections.observableArrayList();
    private final ObservableList<ReminderItem> reminders = FXCollections.observableArrayList();

    private final FilteredList<UserItem> filteredUsers = new FilteredList<>(users);
    private final FilteredList<PetItem> filteredPets = new FilteredList<>(pets);
    private final FilteredList<AppointmentItem> filteredAppointments = new FilteredList<>(appointments);
    private final FilteredList<ReminderItem> filteredReminders = new FilteredList<>(reminders);

    private Label usersCountLabel;
    private Label petsCountLabel;
    private Label appointmentsCountLabel;
    private Label remindersCountLabel;
    private Label statusLabel;
    private Button refreshButton;
    private TextField baseUrlField;
    private TextField searchField;
    private TextArea detailArea;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(buildTopBar());
        root.setCenter(buildContent());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1280, 780);
        scene.getStylesheets().add(getClass().getResource("/styles/admin.css").toExternalForm());

        stage.setTitle("PetCare Admin Panel");
        stage.setScene(scene);
        stage.show();

        applyFilters();
        refreshAllData();
    }

    private HBox buildTopBar() {
        Label title = new Label("PetCare Admin Panel");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Backend yonetimi, kayit izleme ve hizli durum takibi");
        subtitle.getStyleClass().add("page-subtitle");

        VBox titleBox = new VBox(4, title, subtitle);

        baseUrlField = new TextField(apiClient.getBaseUrl());
        baseUrlField.setPromptText("Backend URL");
        baseUrlField.setPrefWidth(270);

        refreshButton = new Button("Yenile");
        refreshButton.getStyleClass().add("primary-button");
        refreshButton.setOnAction(event -> refreshAllData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(14, titleBox, spacer, new Label("API URL:"), baseUrlField, refreshButton);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        return topBar;
    }

    private VBox buildContent() {
        searchField = new TextField();
        searchField.setPromptText("Kullanici, pet, randevu veya hatirlatma ara...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        detailArea = new TextArea("Bir kayit secildiginde detaylari burada gorunecek.");
        detailArea.getStyleClass().add("detail-area");
        detailArea.setEditable(false);
        detailArea.setWrapText(true);
        detailArea.setPrefRowCount(7);

        VBox content = new VBox(16, buildDashboardCards(), searchField, buildTabPane(), buildDetailPanel());
        content.setPadding(new Insets(18));
        return content;
    }

    private GridPane buildDashboardCards() {
        usersCountLabel = createCountLabel();
        petsCountLabel = createCountLabel();
        appointmentsCountLabel = createCountLabel();
        remindersCountLabel = createCountLabel();

        GridPane cards = new GridPane();
        cards.setHgap(16);
        cards.setVgap(16);
        cards.add(createSummaryCard("Kullanicilar", usersCountLabel), 0, 0);
        cards.add(createSummaryCard("Evcil Hayvanlar", petsCountLabel), 1, 0);
        cards.add(createSummaryCard("Randevular", appointmentsCountLabel), 2, 0);
        cards.add(createSummaryCard("Hatirlatmalar", remindersCountLabel), 3, 0);
        return cards;
    }

    private VBox createSummaryCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        VBox card = new VBox(8, titleLabel, valueLabel);
        card.getStyleClass().add("summary-card");
        card.setPrefWidth(250);
        return card;
    }

    private Label createCountLabel() {
        Label label = new Label("0");
        label.getStyleClass().add("card-value");
        return label;
    }

    private TabPane buildTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createUsersTab(),
                createPetsTab(),
                createAppointmentsTab(),
                createRemindersTab()
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return tabPane;
    }

    private Tab createUsersTab() {
        TableView<UserItem> table = new TableView<>(filteredUsers);
        table.getColumns().add(createTextColumn("ID", user -> stringValue(user.userId()), 80));
        table.getColumns().add(createTextColumn("Ad Soyad", UserItem::fullName, 190));
        table.getColumns().add(createTextColumn("E-posta", UserItem::email, 240));
        table.getColumns().add(createTextColumn("Telefon", user -> stringValue(user.phone()), 160));
        table.getColumns().add(createTextColumn("Durum", user -> user.isActive() ? "Active" : "Passive", 120));
        table.getColumns().add(createTextColumn("Olusturma", user -> formatDateTime(user.createdAt()), 170));
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> showUserDetails(item));
        return new Tab("Kullanicilar", table);
    }

    private Tab createPetsTab() {
        TableView<PetItem> table = new TableView<>(filteredPets);
        table.getColumns().add(createTextColumn("ID", pet -> stringValue(pet.petId()), 80));
        table.getColumns().add(createTextColumn("User ID", pet -> stringValue(pet.userId()), 90));
        table.getColumns().add(createTextColumn("Pet Adi", PetItem::petName, 170));
        table.getColumns().add(createTextColumn("Tur", PetItem::species, 140));
        table.getColumns().add(createTextColumn("Irk", pet -> stringValue(pet.breed()), 160));
        table.getColumns().add(createTextColumn("Cinsiyet", pet -> stringValue(pet.gender()), 120));
        table.getColumns().add(createTextColumn("Dogum", pet -> formatDate(pet.birthDate()), 130));
        table.getColumns().add(createTextColumn("Kilo", pet -> pet.currentWeight() == null ? "-" : pet.currentWeight().toPlainString(), 100));
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> showPetDetails(item));
        return new Tab("Evcil Hayvanlar", table);
    }

    private Tab createAppointmentsTab() {
        TableView<AppointmentItem> table = new TableView<>(filteredAppointments);
        table.getColumns().add(createTextColumn("ID", item -> stringValue(item.appointmentId()), 80));
        table.getColumns().add(createTextColumn("Pet ID", item -> stringValue(item.petId()), 90));
        table.getColumns().add(createTextColumn("Veteriner", AppointmentItem::vetName, 190));
        table.getColumns().add(createTextColumn("Klinik", item -> stringValue(item.clinicName()), 190));
        table.getColumns().add(createTextColumn("Tarih", item -> formatDateTime(item.appointmentTime()), 170));
        table.getColumns().add(createStatusColumn(AppointmentItem::status));
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> showAppointmentDetails(item));
        return new Tab("Randevular", table);
    }

    private Tab createRemindersTab() {
        TableView<ReminderItem> table = new TableView<>(filteredReminders);
        table.getColumns().add(createTextColumn("ID", item -> stringValue(item.reminderId()), 80));
        table.getColumns().add(createTextColumn("Pet ID", item -> stringValue(item.petId()), 90));
        table.getColumns().add(createTextColumn("Tip", ReminderItem::reminderType, 130));
        table.getColumns().add(createTextColumn("Baslik", ReminderItem::title, 260));
        table.getColumns().add(createTextColumn("Hatirlatma", item -> formatDateTime(item.remindAt()), 170));
        table.getColumns().add(createStatusColumn(ReminderItem::status));
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> showReminderDetails(item));
        return new Tab("Hatirlatmalar", table);
    }

    private VBox buildDetailPanel() {
        Label title = new Label("Secili Kayit Detayi");
        title.getStyleClass().add("section-title");
        VBox panel = new VBox(8, title, detailArea);
        panel.getStyleClass().add("detail-panel");
        return panel;
    }

    private <T> TableColumn<T, String> createTextColumn(String title, java.util.function.Function<T, String> valueProvider, int width) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(valueProvider.apply(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private <T> TableColumn<T, String> createStatusColumn(java.util.function.Function<T, String> valueProvider) {
        TableColumn<T, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(valueProvider.apply(cellData.getValue())));
        statusCol.setCellFactory(col -> new StatusBadgeCell<>());
        statusCol.setPrefWidth(130);
        return statusCol;
    }

    private HBox buildStatusBar() {
        statusLabel = new Label("Hazir");
        HBox statusBar = new HBox(statusLabel);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        return statusBar;
    }

    private void refreshAllData() {
        apiClient.setBaseUrl(baseUrlField.getText());
        refreshButton.setDisable(true);
        setStatus("Veriler backend uzerinden cekiliyor...");

        Task<Void> task = new Task<>() {
            private List<UserItem> loadedUsers;
            private List<PetItem> loadedPets;
            private List<AppointmentItem> loadedAppointments;
            private List<ReminderItem> loadedReminders;

            @Override
            protected Void call() throws Exception {
                loadedUsers = apiClient.getUsers();
                loadedPets = apiClient.getPets();
                loadedAppointments = apiClient.getAppointments();
                loadedReminders = apiClient.getReminders();
                return null;
            }

            @Override
            protected void succeeded() {
                users.setAll(loadedUsers);
                pets.setAll(loadedPets);
                appointments.setAll(loadedAppointments);
                reminders.setAll(loadedReminders);
                applyFilters();
                setStatus("Veriler basariyla yenilendi.");
                refreshButton.setDisable(false);
            }

            @Override
            protected void failed() {
                setStatus("Veriler alinamadi: " + getException().getMessage());
                refreshButton.setDisable(false);
            }
        };

        Thread worker = new Thread(task, "admin-panel-refresh");
        worker.setDaemon(true);
        worker.start();
    }

    private void applyFilters() {
        String query = normalize(searchField == null ? "" : searchField.getText());
        filteredUsers.setPredicate(user -> query.isBlank() || normalize(user.fullName(), user.email(), user.phone(), user.isActive() ? "active" : "passive").contains(query));
        filteredPets.setPredicate(pet -> query.isBlank() || normalize(pet.petName(), pet.species(), pet.breed(), pet.gender(), stringValue(pet.userId())).contains(query));
        filteredAppointments.setPredicate(item -> query.isBlank() || normalize(item.vetName(), item.clinicName(), item.status(), item.note(), stringValue(item.petId())).contains(query));
        filteredReminders.setPredicate(item -> query.isBlank() || normalize(item.reminderType(), item.title(), item.status(), stringValue(item.petId())).contains(query));
        updateSummaryCards();
    }

    private void updateSummaryCards() {
        usersCountLabel.setText(filteredUsers.size() + " / " + users.size());
        petsCountLabel.setText(filteredPets.size() + " / " + pets.size());
        appointmentsCountLabel.setText(filteredAppointments.size() + " / " + appointments.size());
        remindersCountLabel.setText(filteredReminders.size() + " / " + reminders.size());
    }

    private void showUserDetails(UserItem item) {
        if (item == null) {
            return;
        }
        detailArea.setText("""
                Kullanici
                ID: %s
                Ad Soyad: %s
                E-posta: %s
                Telefon: %s
                Durum: %s
                Olusturma: %s
                """.formatted(
                stringValue(item.userId()),
                stringValue(item.fullName()),
                stringValue(item.email()),
                stringValue(item.phone()),
                item.isActive() ? "Active" : "Passive",
                formatDateTime(item.createdAt())
        ));
    }

    private void showPetDetails(PetItem item) {
        if (item == null) {
            return;
        }
        detailArea.setText("""
                Evcil Hayvan
                ID: %s
                Sahip User ID: %s
                Ad: %s
                Tur / Irk: %s / %s
                Cinsiyet: %s
                Dogum: %s
                Kilo: %s
                Notlar: %s
                """.formatted(
                stringValue(item.petId()),
                stringValue(item.userId()),
                stringValue(item.petName()),
                stringValue(item.species()),
                stringValue(item.breed()),
                stringValue(item.gender()),
                formatDate(item.birthDate()),
                item.currentWeight() == null ? "-" : item.currentWeight().toPlainString(),
                stringValue(item.notes())
        ));
    }

    private void showAppointmentDetails(AppointmentItem item) {
        if (item == null) {
            return;
        }
        detailArea.setText("""
                Randevu
                ID: %s
                Pet ID: %s
                Veteriner: %s
                Klinik: %s
                Tarih: %s
                Durum: %s
                Not: %s
                """.formatted(
                stringValue(item.appointmentId()),
                stringValue(item.petId()),
                stringValue(item.vetName()),
                stringValue(item.clinicName()),
                formatDateTime(item.appointmentTime()),
                stringValue(item.status()),
                stringValue(item.note())
        ));
    }

    private void showReminderDetails(ReminderItem item) {
        if (item == null) {
            return;
        }
        detailArea.setText("""
                Hatirlatma
                ID: %s
                Pet ID: %s
                Tip: %s
                Baslik: %s
                Hatirlatma: %s
                Durum: %s
                Olusturma: %s
                """.formatted(
                stringValue(item.reminderId()),
                stringValue(item.petId()),
                stringValue(item.reminderType()),
                stringValue(item.title()),
                formatDateTime(item.remindAt()),
                stringValue(item.status()),
                formatDateTime(item.createdAt())
        ));
    }

    private void setStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private String formatDate(LocalDate value) {
        return value == null ? "-" : DATE_FORMATTER.format(value);
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : DATE_TIME_FORMATTER.format(value);
    }

    private String stringValue(Object value) {
        return value == null ? "-" : value.toString();
    }

    private String normalize(String... values) {
        return String.join(" ", values).toLowerCase(Locale.ROOT);
    }

    private String normalize(Object... values) {
        StringBuilder builder = new StringBuilder();
        for (Object value : values) {
            if (value != null) {
                builder.append(value).append(' ');
            }
        }
        return builder.toString().toLowerCase(Locale.ROOT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
