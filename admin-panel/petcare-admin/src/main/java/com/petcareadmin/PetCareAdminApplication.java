package com.petcareadmin;

import com.petcareadmin.client.BackendApiClient;
import com.petcareadmin.model.AppointmentItem;
import com.petcareadmin.model.PetItem;
import com.petcareadmin.model.ReminderItem;
import com.petcareadmin.model.UserItem;
import com.petcareadmin.ui.StatusBadgeCell;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PetCareAdminApplication extends Application {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final BackendApiClient apiClient = new BackendApiClient("http://localhost:8080");

    private final ObservableList<UserItem> users = FXCollections.observableArrayList();
    private final ObservableList<PetItem> pets = FXCollections.observableArrayList();
    private final ObservableList<AppointmentItem> appointments = FXCollections.observableArrayList();
    private final ObservableList<ReminderItem> reminders = FXCollections.observableArrayList();

    private Label usersCountLabel;
    private Label petsCountLabel;
    private Label appointmentsCountLabel;
    private Label remindersCountLabel;
    private Label statusLabel;
    private Button refreshButton;
    private TextField baseUrlField;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(buildTopBar());
        root.setCenter(buildContent());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1240, 760);
        scene.getStylesheets().add(getClass().getResource("/styles/admin.css").toExternalForm());

        stage.setTitle("PetCare Admin Panel");
        stage.setScene(scene);
        stage.show();

        refreshAllData();
    }

    private HBox buildTopBar() {
        Label title = new Label("PetCare Admin Panel");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Backend yonetimi ve hizli durum takibi");
        subtitle.getStyleClass().add("page-subtitle");

        VBox titleBox = new VBox(4, title, subtitle);

        baseUrlField = new TextField(apiClient.getBaseUrl());
        baseUrlField.setPromptText("Backend URL");
        baseUrlField.setPrefWidth(280);

        refreshButton = new Button("Verileri Yenile");
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
        VBox content = new VBox(18, buildDashboardCards(), buildTabPane());
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
        card.setPrefWidth(240);
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
        return tabPane;
    }

    private Tab createUsersTab() {
        TableView<UserItem> table = new TableView<>(users);
        table.getColumns().add(createTextColumn("ID", user -> stringValue(user.userId())));
        table.getColumns().add(createTextColumn("Ad Soyad", UserItem::fullName));
        table.getColumns().add(createTextColumn("E-posta", UserItem::email));
        table.getColumns().add(createTextColumn("Telefon", user -> stringValue(user.phone())));
        table.getColumns().add(createTextColumn("Durum", user -> user.isActive() ? "Active" : "Passive"));
        table.getColumns().add(createTextColumn("Olusturma", user -> formatDateTime(user.createdAt())));
        return new Tab("Kullanicilar", table);
    }

    private Tab createPetsTab() {
        TableView<PetItem> table = new TableView<>(pets);
        table.getColumns().add(createTextColumn("ID", pet -> stringValue(pet.petId())));
        table.getColumns().add(createTextColumn("User ID", pet -> stringValue(pet.userId())));
        table.getColumns().add(createTextColumn("Pet Adi", PetItem::petName));
        table.getColumns().add(createTextColumn("Tur", PetItem::species));
        table.getColumns().add(createTextColumn("Irk", pet -> stringValue(pet.breed())));
        table.getColumns().add(createTextColumn("Cinsiyet", pet -> stringValue(pet.gender())));
        table.getColumns().add(createTextColumn("Kilo", pet -> pet.currentWeight() == null ? "-" : pet.currentWeight().toPlainString()));
        return new Tab("Evcil Hayvanlar", table);
    }

    private Tab createAppointmentsTab() {
        TableView<AppointmentItem> table = new TableView<>(appointments);
        table.getColumns().add(createTextColumn("ID", item -> stringValue(item.appointmentId())));
        table.getColumns().add(createTextColumn("Pet ID", item -> stringValue(item.petId())));
        table.getColumns().add(createTextColumn("Veteriner", AppointmentItem::vetName));
        table.getColumns().add(createTextColumn("Klinik", item -> stringValue(item.clinicName())));
        table.getColumns().add(createTextColumn("Tarih", item -> formatDateTime(item.appointmentTime())));

        // Custom Graphics: StatusBadgeCell — Canvas ile çizilen renkli badge
        TableColumn<AppointmentItem, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().status()));
        statusCol.setCellFactory(col -> new StatusBadgeCell<>());
        statusCol.setPrefWidth(130);
        table.getColumns().add(statusCol);

        return new Tab("Randevular", table);
    }

    private Tab createRemindersTab() {
        TableView<ReminderItem> table = new TableView<>(reminders);
        table.getColumns().add(createTextColumn("ID", item -> stringValue(item.reminderId())));
        table.getColumns().add(createTextColumn("Pet ID", item -> stringValue(item.petId())));
        table.getColumns().add(createTextColumn("Tip", ReminderItem::reminderType));
        table.getColumns().add(createTextColumn("Baslik", ReminderItem::title));
        table.getColumns().add(createTextColumn("Hatirlatma", item -> formatDateTime(item.remindAt())));

        // Custom Graphics: StatusBadgeCell — Canvas ile çizilen renkli badge
        TableColumn<ReminderItem, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().status()));
        statusCol.setCellFactory(col -> new StatusBadgeCell<>());
        statusCol.setPrefWidth(130);
        table.getColumns().add(statusCol);

        return new Tab("Hatirlatmalar", table);
    }

    private <T> TableColumn<T, String> createTextColumn(String title, java.util.function.Function<T, String> valueProvider) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(valueProvider.apply(cellData.getValue())));
        column.setPrefWidth(160);
        return column;
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
                updateSummaryCards();
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

    private void updateSummaryCards() {
        usersCountLabel.setText(String.valueOf(users.size()));
        petsCountLabel.setText(String.valueOf(pets.size()));
        appointmentsCountLabel.setText(String.valueOf(appointments.size()));
        remindersCountLabel.setText(String.valueOf(reminders.size()));
    }

    private void setStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private String formatDateTime(java.time.LocalDateTime value) {
        return value == null ? "-" : DATE_TIME_FORMATTER.format(value);
    }

    private String stringValue(Object value) {
        return value == null ? "-" : value.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
