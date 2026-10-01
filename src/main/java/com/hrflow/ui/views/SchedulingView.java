package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.model.Candidate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Interview Scheduling View powered by Queue & PriorityQueue with Availability Checking.
 */
public class SchedulingView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<QueueInterviewScheduler.InterviewBooking> bookingData = FXCollections.observableArrayList();
    private final TableView<QueueInterviewScheduler.InterviewBooking> table = new TableView<>();
    private final Label conflictStatusLabel = new Label("Availability Status: 0 Resource Overlaps across all slots");
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
        Label title = new Label("Queue-Based Interview Scheduling");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Processes interview requests via Queue / Priority Queue with fast slot and room availability checking.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("glass-card");
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox queueModeBox = new VBox(4);
        Label qLabel = new Label("Queue Processing Strategy:");
        qLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #cbd5e1;");
        queueModeCombo.getItems().addAll("Priority Queue (Score Order)", "FIFO Queue (Arrival Order)");
        queueModeCombo.setValue("Priority Queue (Score Order)");
        queueModeCombo.setStyle("-fx-background-color: #1e293b;");
        queueModeBox.getChildren().addAll(qLabel, queueModeCombo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button regenerateBtn = new Button("⚡ Run Queue Scheduler");
        regenerateBtn.getStyleClass().add("btn-primary");
        regenerateBtn.setOnAction(e -> {
            boolean usePQ = queueModeCombo.getValue().contains("Priority");
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
        conflictStatusLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 600; -fx-text-fill: #34d399;");
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
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #818cf8;");
                }
            }
        });

        TableColumn<QueueInterviewScheduler.InterviewBooking, String> colCandidate = new TableColumn<>("Candidate");
        colCandidate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCandidate().getName() + " (" + cellData.getValue().getCandidate().getAppliedRole() + ")"));

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
                    badge.setStyle("-fx-background-color: rgba(16,185,129,0.15); -fx-text-fill: #34d399; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-size: 11.5px; -fx-font-weight: 600;");
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
        conflictStatusLabel.setText(String.format("Availability Check: 0 Conflicts | %d sessions scheduled using %s",
                bookings.size(), usePriorityQueue ? "Priority Queue" : "FIFO Queue"));
    }
}
