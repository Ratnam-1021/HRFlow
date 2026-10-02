package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.model.Candidate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import com.hrflow.service.ProfileImageService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Interview Scheduling View powered by Queue & PriorityQueue with Availability Checking.
 * Soft Warm Light Yellowish-Orange Theme.
 */
public class SchedulingView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<QueueInterviewScheduler.InterviewBooking> bookingData = FXCollections.observableArrayList();
    private final TableView<QueueInterviewScheduler.InterviewBooking> table = new TableView<>();
    private final Label conflictStatusLabel = new Label("✓ All interview slots verified — No scheduling conflicts");
    private final ComboBox<String> queueModeCombo = new ComboBox<>();

    public SchedulingView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildControlBar();
        buildTable();

        generateSchedule(true);
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Interview Scheduling & Calendar");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #261B12;");
        Label subtitle = new Label("Coordinate interview sessions with automatic room allocation and conflict-free interviewer scheduling.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #786958;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("glass-card");
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox queueModeBox = new VBox(4);
        Label qLabel = new Label("Scheduling Order:");
        qLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #5C4D3E;");
        queueModeCombo.getItems().addAll("Top Fit Candidates First", "Chronological (Application Order)");
        queueModeCombo.setValue("Top Fit Candidates First");
        queueModeBox.getChildren().addAll(qLabel, queueModeCombo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button regenerateBtn = new Button("⚡ Generate Schedule");
        regenerateBtn.getStyleClass().add("btn-primary");
        regenerateBtn.setOnAction(e -> {
            boolean usePQ = queueModeCombo.getValue().contains("Top Fit");
            generateSchedule(usePQ);
        });

        bar.getChildren().addAll(queueModeBox, spacer, regenerateBtn);
        getChildren().add(bar);
    }

    private void buildTable() {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        conflictStatusLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 700; -fx-text-fill: #15803D;");
        statusRow.getChildren().add(conflictStatusLabel);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<QueueInterviewScheduler.InterviewBooking, String> colId = new TableColumn<>("Booking Ref");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(110);

        TableColumn<QueueInterviewScheduler.InterviewBooking, LocalTime> colTime = new TableColumn<>("Time Slot");
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colTime.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) setText(null);
                else {
                    setText(time.toString());
                    setStyle("-fx-font-weight: 700; -fx-text-fill: #C2410C;");
                }
            }
        });

        TableColumn<QueueInterviewScheduler.InterviewBooking, QueueInterviewScheduler.InterviewBooking> colCandidate = new TableColumn<>("Candidate");
        colCandidate.setMinWidth(190);
        colCandidate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colCandidate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(QueueInterviewScheduler.InterviewBooking booking, boolean empty) {
                super.updateItem(booking, empty);
                if (empty || booking == null || booking.getCandidate() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Candidate c = booking.getCandidate();
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);

                    Node avatar = ProfileImageService.createAvatarNode(c, 30);

                    VBox nameBox = new VBox(2);
                    Label nameLbl = new Label(c.getName());
                    nameLbl.setStyle("-fx-font-weight: 700; -fx-font-size: 13px; -fx-text-fill: #1A110B;");
                    Label roleLbl = new Label(c.getAppliedRole());
                    roleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8C7A6B;");
                    nameBox.getChildren().addAll(nameLbl, roleLbl);

                    box.getChildren().addAll(avatar, nameBox);
                    setGraphic(box);
                    setText(null);
                }
            }
        });

        TableColumn<QueueInterviewScheduler.InterviewBooking, String> colInterviewer = new TableColumn<>("Assigned Interviewer");
        colInterviewer.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getInterviewer().getName() + " - " + cellData.getValue().getInterviewer().getRole()));

        TableColumn<QueueInterviewScheduler.InterviewBooking, String> colRoom = new TableColumn<>("Allocated Room");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colRoom.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(room);
                    badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    setGraphic(badge);
                }
            }
        });

        table.getColumns().addAll(colId, colTime, colCandidate, colInterviewer, colRoom);
        table.setItems(bookingData);

        card.getChildren().addAll(statusRow, table);
        getChildren().add(card);
    }

    private void generateSchedule(boolean usePriorityQueue) {
        List<Candidate> top = context.getCandidateService().rankCandidates(Set.of("Java", "Spring Boot", "SQL"), 8);
        List<QueueInterviewScheduler.InterviewBooking> bookings = context.getSchedulingService().autoSchedule(
                top, context.getSchedulingService().getInterviewPanel(), LocalDate.now().plusDays(1), usePriorityQueue
        );
        bookingData.setAll(bookings);
        conflictStatusLabel.setText(String.format("✓ %d interview sessions scheduled without conflicts (%s)",
                bookings.size(), usePriorityQueue ? "Top Fit First" : "Application Order"));
    }
}
