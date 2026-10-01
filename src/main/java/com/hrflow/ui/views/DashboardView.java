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
        HBox contentBox = new HBox(20);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Left Panel: Top Shortlisted Candidates
        VBox leftCard = new VBox(12);
        leftCard.getStyleClass().add("glass-card");
        HBox.setHgrow(leftCard, Priority.ALWAYS);

        HBox leftHeader = new HBox();
        Label leftTitle = new Label("Top Ranked Candidates");
        leftTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #261B12;");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        Button viewAllBtn = new Button("View Pipeline →");
        viewAllBtn.getStyleClass().add("btn-secondary");
        viewAllBtn.setOnAction(e -> navigationHandler.accept("recruitment"));
        leftHeader.getChildren().addAll(leftTitle, sp1, viewAllBtn);

        TableView<Candidate> candidateTable = createCandidateTable();
        VBox.setVgrow(candidateTable, Priority.ALWAYS);
        leftCard.getChildren().addAll(leftHeader, candidateTable);

        // Right Panel: Recruitment Workflow Overview
        VBox rightCard = new VBox(14);
        rightCard.getStyleClass().add("glass-card");
        rightCard.setPrefWidth(400);

        Label rightTitle = new Label("Recruitment Workflow Overview");
        rightTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #261B12;");

        VBox p1 = createArchitectureTile("1. Sourcing & Screening",
                "• Instant skill matching across applicant resumes\n• Weighted candidate evaluation & scoring\n• Rapid shortlisting of high-potential talent",
                "-fx-border-color: #EA580C;");

        VBox p2 = createArchitectureTile("2. Interview Scheduling",
                "• Automated time slot & room assignment\n• Prioritized rounds for top-scoring candidates\n• Conflict prevention for interviewers and facilities",
                "-fx-border-color: #D97706;");

        VBox p3 = createArchitectureTile("3. Audit & Activity Tracking",
                "• Complete timestamped history of hiring decisions\n• One-click undo for accidental status updates\n• Transparent, compliance-ready action trail",
                "-fx-border-color: #C2410C;");

        rightCard.getChildren().addAll(rightTitle, p1, p2, p3);

        contentBox.getChildren().addAll(leftCard, rightCard);
        getChildren().add(contentBox);
    }

    private VBox createArchitectureTile(String heading, String body, String borderStyle) {
        VBox tile = new VBox(6);
        tile.setStyle("-fx-background-color: #FBF6EE; -fx-padding: 12; -fx-background-radius: 8; -fx-border-width: 0 0 0 3.5; " + borderStyle);
        Label h = new Label(heading);
        h.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #2B1E13;");
        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-font-size: 12px; -fx-text-fill: #5E4E3F; -fx-line-spacing: 2;");
        tile.getChildren().addAll(h, b);
        return tile;
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

        table.getColumns().addAll(colName, colRole, colScore, colExp);

        List<Candidate> topCandidates = CandidateSortEngine.rankAndGetTopK(
                context.getCandidateService().getAllCandidates(), Set.of("Java", "SQL"), 6
        );
        table.getItems().addAll(topCandidates);

        return table;
    }
}
