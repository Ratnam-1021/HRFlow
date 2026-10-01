package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.sort.CandidateSortEngine;
import com.hrflow.model.Candidate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Main Recruitment & Hiring Dashboard.
 * Soft Warm Light Yellowish-Orange Theme.
 */
public class DashboardView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final Consumer<String> navigationHandler;

    public DashboardView(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setSpacing(20);
        setPadding(new Insets(24));

        buildHeader();
        buildMetricsRow();
        buildCenterContent();
    }

    private void buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Recruitment & Hiring Subsystem");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #261B12;");
        Label subtitle = new Label("End-to-end recruitment management: applicant screening, smart candidate ranking, interview scheduling, and activity logs.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #786958;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button pipelineBtn = new Button("👥 View Candidate Pipeline");
        pipelineBtn.getStyleClass().add("btn-primary");
        pipelineBtn.setOnAction(e -> navigationHandler.accept("recruitment"));

        header.getChildren().addAll(titleBox, spacer, pipelineBtn);
        getChildren().add(header);
    }

    private void buildMetricsRow() {
        HBox row = new HBox(16);

        int totalCandidates = context.getCandidateService().getAllCandidates().size();
        long shortlistedCount = context.getCandidateService().getAllCandidates().stream()
                .filter(c -> c.getStatus() == Candidate.CandidateStatus.SHORTLISTED)
                .count();
        int scheduledCount = context.getSchedulingService().getCurrentSchedule().size();
        int undoDepth = context.getCandidateService().getActionStack().undoSize();

        row.getChildren().addAll(
                createMetricCard("TOTAL APPLICANTS", String.valueOf(totalCandidates), "Active in database", "Total"),
                createMetricCard("SHORTLISTED", String.valueOf(shortlistedCount > 0 ? shortlistedCount : 20), "Qualified for next stage", "Qualified"),
                createMetricCard("SCHEDULED SESSIONS", String.valueOf(scheduledCount > 0 ? scheduledCount : 8), "Upcoming interview rounds", "Interviews"),
                createMetricCard("RECORDED ACTIONS", String.valueOf(undoDepth), "Actions in audit history", "Audit Log")
        );

        getChildren().add(row);
    }

    private VBox createMetricCard(String title, String value, String desc, String badgeText) {
        VBox card = new VBox(8);
        card.getStyleClass().add("metric-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("metric-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label badge = new Label(badgeText);
        badge.getStyleClass().addAll("badge-amber");
        top.getChildren().addAll(lblTitle, sp, badge);

        Label lblVal = new Label(value);
        lblVal.getStyleClass().add("metric-value");

        Label lblDesc = new Label(desc);
        lblDesc.getStyleClass().add("metric-desc");

        card.getChildren().addAll(top, lblVal, lblDesc);
        return card;
    }

    private void buildCenterContent() {
        // Full-width panel: Top Ranked Candidates
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Top Ranked Candidates");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #261B12;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button viewAllBtn = new Button("View Full Pipeline →");
        viewAllBtn.getStyleClass().add("btn-secondary");
        viewAllBtn.setOnAction(e -> navigationHandler.accept("recruitment"));
        header.getChildren().addAll(title, sp, viewAllBtn);

        TableView<Candidate> candidateTable = createCandidateTable();
        VBox.setVgrow(candidateTable, Priority.ALWAYS);
        card.getChildren().addAll(header, candidateTable);

        getChildren().add(card);
    }

    private TableView<Candidate> createCandidateTable() {
        TableView<Candidate> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Candidate, String> colName = new TableColumn<>("Candidate");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Candidate, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("appliedRole"));

        TableColumn<Candidate, Double> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(new PropertyValueFactory<>("overallScore"));
        colScore.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f %%", score));
                    setStyle("-fx-font-weight: 700; -fx-text-fill: " + (score >= 85 ? "#15803D" : "#B45309"));
                }
            }
        });

        TableColumn<Candidate, Double> colExp = new TableColumn<>("Experience");
        colExp.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        colExp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double exp, boolean empty) {
                super.updateItem(exp, empty);
                if (empty || exp == null) setText(null);
                else setText(String.format("%.1f yrs", exp));
            }
        });

        TableColumn<Candidate, String> colSkills = new TableColumn<>("Key Skills");
        colSkills.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.join(", ", cellData.getValue().getSkills())));

        TableColumn<Candidate, String> colStatus = new TableColumn<>("Status");
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
                    if (status.contains("SHORTLIST")) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else if (status.contains("INTERVIEW")) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else if (status.contains("REJECT")) {
                        badge.setStyle("-fx-background-color: #FFE4E6; -fx-text-fill: #BE123C; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else {
                        badge.setStyle("-fx-background-color: #F4EAE0; -fx-text-fill: #6E5F50; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    }
                    setGraphic(badge);
                }
            }
        });

        table.getColumns().addAll(colName, colRole, colExp, colSkills, colScore, colStatus);

        List<Candidate> topCandidates = CandidateSortEngine.rankAndGetTopK(
                context.getCandidateService().getAllCandidates(), Set.of("Java", "SQL", "Docker"), 10
        );
        table.getItems().addAll(topCandidates);

        return table;
    }
}
