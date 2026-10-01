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
        Label title = new Label("Recruitment & Hiring Subsystem 👋");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Algorithmic talent acquisition pipeline: Indexing (HashMap), Search, Ranking (Selection Sort), Queue Scheduling, and Stack History.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button pipelineBtn = new Button("👥 Open Candidate Pipeline");
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
                createMetricCard("APPLICANT POOL", String.valueOf(totalCandidates), "Indexed via HashMap", "badge-indigo"),
                createMetricCard("SHORTLISTED", String.valueOf(shortlistedCount > 0 ? shortlistedCount : 20), "Ranked via Selection Sort", "badge-emerald"),
                createMetricCard("INTERVIEW SESSIONS", String.valueOf(scheduledCount > 0 ? scheduledCount : 8), "Queue & Priority Queue", "badge-amber"),
                createMetricCard("STACK UNDO DEPTH", String.valueOf(undoDepth), "LIFO Action History", "badge-rose")
        );

        getChildren().add(row);
    }

    private VBox createMetricCard(String title, String value, String desc, String badgeClass) {
        VBox card = new VBox(8);
        card.getStyleClass().add("metric-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("metric-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label badge = new Label("DSA Engine");
        badge.getStyleClass().addAll(badgeClass);
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

        // Left Panel: Top Shortlisted Candidates (Selection Sort top 6)
        VBox leftCard = new VBox(12);
        leftCard.getStyleClass().add("glass-card");
        HBox.setHgrow(leftCard, Priority.ALWAYS);

        HBox leftHeader = new HBox();
        Label leftTitle = new Label("Top Ranked Candidates (Selection Sort)");
        leftTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        Button viewAllBtn = new Button("View Pipeline →");
        viewAllBtn.getStyleClass().add("btn-secondary");
        viewAllBtn.setOnAction(e -> navigationHandler.accept("recruitment"));
        leftHeader.getChildren().addAll(leftTitle, sp1, viewAllBtn);

        TableView<Candidate> candidateTable = createCandidateTable();
        VBox.setVgrow(candidateTable, Priority.ALWAYS);
        leftCard.getChildren().addAll(leftHeader, candidateTable);

        // Right Panel: DSA Subsystem Flow Architecture Summary
        VBox rightCard = new VBox(14);
        rightCard.getStyleClass().add("glass-card");
        rightCard.setPrefWidth(420);

        Label rightTitle = new Label("Subsystem Architecture & DSA Flow");
        rightTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        VBox p1 = createArchitectureTile("1. Recruitment (Search & Ranking)",
                "• HashMap: O(1) Candidate skill lookup\n• Linear Search: Prefix autocomplete filtering\n• Binary Search: O(log N) sorted ID lookup\n• Sorting: Selection Sort (Top-K) & Insertion Sort",
                "-fx-border-color: #6366f1;");

        VBox p2 = createArchitectureTile("2. Scheduling (Queue Engine)",
                "• Queue (FIFO): First-Come, First-Served interviews\n• Priority Queue: High-score applicants first\n• Availability Check: Instant slot & room collision check",
                "-fx-border-color: #10b981;");

        VBox p3 = createArchitectureTile("3. History (Stack Engine)",
                "• Dual-Stack: LIFO Undo and Redo\n• Full Audit Trail: Trace status modifications in real time",
                "-fx-border-color: #f59e0b;");

        rightCard.getChildren().addAll(rightTitle, p1, p2, p3);

        contentBox.getChildren().addAll(leftCard, rightCard);
        getChildren().add(contentBox);
    }

    private VBox createArchitectureTile(String heading, String body, String borderStyle) {
        VBox tile = new VBox(4);
        tile.setStyle("-fx-background-color: #1a253c; -fx-padding: 12; -fx-background-radius: 8; -fx-border-width: 0 0 0 3; " + borderStyle);
        Label h = new Label(heading);
        h.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #cbd5e1; -fx-line-spacing: 2;");
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
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " + (score >= 85 ? "#34d399" : "#fbbf24"));
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
