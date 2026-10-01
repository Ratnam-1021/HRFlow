package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.interval.LeaveOverlapDetector;
import com.hrflow.model.LeaveRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Leave Management & Sweep-Line Bottleneck Analysis View.
 */
public class LeavesView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<LeaveRequest> leaveData = FXCollections.observableArrayList();
    private final TableView<LeaveRequest> table = new TableView<>();
    private final VBox alertsContainer = new VBox(10);

    public LeavesView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildConflictBanner();
        buildTable();

        refreshData();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Leave Auditing & Absence Overlap Detection");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Sweep-Line interval algorithm detects simultaneous departmental absences before operational risks occur.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildConflictBanner() {
        alertsContainer.getChildren().clear();
        List<LeaveOverlapDetector.ConflictReport> conflicts = context.getLeaveService().detectConflicts(2);

        if (conflicts.isEmpty()) {
            HBox safeBanner = new HBox(12);
            safeBanner.setAlignment(Pos.CENTER_LEFT);
            safeBanner.setStyle("-fx-background-color: rgba(16,185,129,0.15); -fx-border-color: #10b981; -fx-border-radius: 8; -fx-padding: 12 16; -fx-background-radius: 8;");
            Label icon = new Label("🛡️");
            Label msg = new Label("All team capacity safe: No concurrent departmental leave overlaps exceeding quorum threshold.");
            msg.setStyle("-fx-text-fill: #34d399; -fx-font-weight: 600; -fx-font-size: 13px;");
            safeBanner.getChildren().addAll(icon, msg);
            alertsContainer.getChildren().add(safeBanner);
        } else {
            for (LeaveOverlapDetector.ConflictReport cr : conflicts) {
                VBox alertCard = new VBox(6);
                alertCard.setStyle("-fx-background-color: rgba(244,63,94,0.15); -fx-border-color: #f43f5e; -fx-border-radius: 8; -fx-padding: 12 16; -fx-background-radius: 8;");

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                Label badge = new Label("CRITICAL BOTTLENECK ALERT");
                badge.setStyle("-fx-background-color: #f43f5e; -fx-text-fill: #ffffff; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 6;");
                Label title = new Label(String.format("%s Department: Peak %d simultaneous absences on %s",
                        cr.getDepartment().getDisplayName(), cr.getPeakConcurrentLeaves(), cr.getPeakDate()));
                title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13.5px;");
                header.getChildren().addAll(badge, title);

                Label desc = new Label(cr.getMessage() + ". Immediate HR managerial review required.");
                desc.setStyle("-fx-text-fill: #fecdd3; -fx-font-size: 12px;");

                alertCard.getChildren().addAll(header, desc);
                alertsContainer.getChildren().add(alertCard);
            }
        }

        getChildren().add(alertsContainer);
    }

    private void buildTable() {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<LeaveRequest, String> colId = new TableColumn<>("Request Ref");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(110);

        TableColumn<LeaveRequest, String> colEmp = new TableColumn<>("Employee Name");
        colEmp.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<LeaveRequest, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDepartment().getDisplayName()));

        TableColumn<LeaveRequest, LocalDate> colStart = new TableColumn<>("Start Date");
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveRequest, LocalDate> colEnd = new TableColumn<>("End Date");
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveRequest, String> colType = new TableColumn<>("Leave Category");
        colType.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getType().name()));

        TableColumn<LeaveRequest, String> colStatus = new TableColumn<>("Audit Status");
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().name()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-weight: bold; " +
                            (status.contains("CONFLICT") ? "-fx-background-color: rgba(244,63,94,0.25); -fx-text-fill: #fb7185;" :
                             status.contains("APPROVED") ? "-fx-background-color: rgba(16,185,129,0.2); -fx-text-fill: #34d399;" :
                             "-fx-background-color: rgba(245,158,11,0.2); -fx-text-fill: #fbbf24;"));
                    setGraphic(badge);
                }
            }
        });

        table.getColumns().addAll(colId, colEmp, colDept, colStart, colEnd, colType, colStatus);
        table.setItems(leaveData);

        card.getChildren().add(table);
        getChildren().add(card);
    }

    private void refreshData() {
        leaveData.setAll(context.getLeaveService().getAllLeaveRequests());
    }
}
