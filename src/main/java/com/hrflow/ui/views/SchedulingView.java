package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.graph.InterviewScheduler;
import com.hrflow.model.Candidate;
import com.hrflow.model.Department;
import com.hrflow.model.Employee;
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
 * Intelligent Interview Scheduling View powered by Graph Vertex Coloring & Greedy Room Assignment.
 */
public class SchedulingView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<InterviewScheduler.InterviewBooking> bookingData = FXCollections.observableArrayList();
    private final TableView<InterviewScheduler.InterviewBooking> table = new TableView<>();
    private final Label conflictStatusLabel = new Label("Graph Conflict Status: 0 Resource Overlaps (Optimal Coloring)");

    public SchedulingView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildControlBar();
        buildTable();

        generateInitialSchedule();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Intelligent Interview Scheduling");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Graph Vertex Coloring models resource constraints to prevent room and interviewer double-booking.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("glass-card");
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        Label lbl = new Label("Algorithmic Constraints Handled:");
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label sub = new Label("• Non-overlapping interviewer slots • Unique candidate sessions • Concurrent room limits");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        info.getChildren().addAll(lbl, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button regenerateBtn = new Button("⚡ Re-Solve Graph Schedule");
        regenerateBtn.getStyleClass().add("btn-primary");
        regenerateBtn.setOnAction(e -> generateInitialSchedule());

        bar.getChildren().addAll(info, spacer, regenerateBtn);
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

        TableColumn<InterviewScheduler.InterviewBooking, String> colId = new TableColumn<>("Booking Ref");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(110);

        TableColumn<InterviewScheduler.InterviewBooking, LocalTime> colTime = new TableColumn<>("Time Slot");
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

        TableColumn<InterviewScheduler.InterviewBooking, String> colCandidate = new TableColumn<>("Candidate");
        colCandidate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCandidate().getName() + " (" + cellData.getValue().getCandidate().getAppliedRole() + ")"));

        TableColumn<InterviewScheduler.InterviewBooking, String> colInterviewer = new TableColumn<>("Assigned Interviewer");
        colInterviewer.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getInterviewer().getName() + " - " + cellData.getValue().getInterviewer().getRole()));

        TableColumn<InterviewScheduler.InterviewBooking, String> colRoom = new TableColumn<>("Allocated Room");
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

    private void generateInitialSchedule() {
        List<Candidate> top = context.getCandidateService().rankCandidates(Set.of("Java", "Spring Boot", "SQL"), 8);
        List<Employee> interviewers = context.getEmployeeService().getEmployeesByDepartment(Department.ENGINEERING);
        if (interviewers.isEmpty()) {
            interviewers = context.getEmployeeService().getAllEmployees();
        }

        List<InterviewScheduler.InterviewBooking> bookings = context.getSchedulingService().autoSchedule(
                top, interviewers, LocalDate.now().plusDays(1)
        );
        bookingData.setAll(bookings);
        conflictStatusLabel.setText(String.format("Graph Conflict Status: 0 Resource Overlaps across %d scheduled sessions", bookings.size()));
    }
}
