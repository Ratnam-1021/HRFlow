package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.service.AutomationCenterService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Automation Center: One-Click End-to-End Algorithmic HR Operations.
 */
public class AutomationCenterView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final VBox logContainer = new VBox(10);
    private final ProgressBar progressBar = new ProgressBar(0);
    private final Label statusLabel = new Label("Ready to initiate daily automated workforce pipeline.");
    private final Button runBtn = new Button("🚀 Run Daily Automation");

    public AutomationCenterView() {
        setSpacing(20);
        setPadding(new Insets(24));

        buildHeader();
        buildActionBanner();
        buildLogPanel();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("HR Automation Center");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Execute full-stack DSA orchestration across recruitment, interval conflict audits, and interview scheduling.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildActionBanner() {
        HBox banner = new HBox(20);
        banner.getStyleClass().add("glass-card");
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setStyle(banner.getStyle() + "; -fx-border-color: #6366f1; -fx-border-width: 1.5;");

        VBox info = new VBox(6);
        Label heading = new Label("Automated Daily Workforce Operations");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label desc = new Label("Triggers: 1) MaxHeap applicant re-scoring  2) Graph interview coloring  3) Sweep-line absence collision check  4) Task priority dispatch.");
        desc.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #cbd5e1;");
        info.getChildren().addAll(heading, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        runBtn.getStyleClass().addAll("btn-emerald");
        runBtn.setStyle("-fx-font-size: 14px; -fx-padding: 12 24;");
        runBtn.setOnAction(e -> triggerAutomation());

        banner.getChildren().addAll(info, spacer, runBtn);
        getChildren().add(banner);
    }

    private void buildLogPanel() {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox statusRow = new HBox(12);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #38bdf8;");

        progressBar.setProgress(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBar.setStyle("-fx-accent: #10b981;");

        statusRow.getChildren().addAll(statusLabel, progressBar);

        ScrollPane scroll = new ScrollPane(logContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: #1e293b; -fx-border-radius: 8;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Prepopulate introductory check items
        addCheckItem("Candidate screening ready (250+ resumes in database)");
        addCheckItem("Graph vertex coloring engine initialized for panel interviews");
        addCheckItem("Sweep-line interval detector listening for departmental overlaps");
        addCheckItem("Task Priority Heap synchronized with active company OKRs");

        card.getChildren().addAll(statusRow, scroll);
        getChildren().add(card);
    }

    private void triggerAutomation() {
        runBtn.setDisable(true);
        logContainer.getChildren().clear();
        progressBar.setProgress(-1); // indeterminate
        statusLabel.setText("Running full DSA automation sequence...");

        context.getAutomationCenterService().runDailyAutomationAsync(msg -> {
            Platform.runLater(() -> addLogMessage(msg));
        }).thenAccept(result -> {
            Platform.runLater(() -> {
                progressBar.setProgress(1.0);
                runBtn.setDisable(false);
                statusLabel.setText(String.format("Completed successfully in %d ms!", result.executionTimeMillis));
                addSuccessSummary(result);
            });
        });
    }

    private void addCheckItem(String text) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("•");
        icon.setStyle("-fx-text-fill: #6366f1; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        item.getChildren().addAll(icon, lbl);
        logContainer.getChildren().add(item);
    }

    private void addLogMessage(String msg) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("✓");
        icon.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label lbl = new Label(msg);
        lbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
        item.getChildren().addAll(icon, lbl);
        logContainer.getChildren().add(item);
    }

    private void addSuccessSummary(AutomationCenterService.AutomationResult result) {
        VBox summaryBox = new VBox(8);
        summaryBox.setStyle("-fx-background-color: #1e293b; -fx-padding: 16; -fx-background-radius: 8; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-margin: 10 0;");

        Label heading = new Label("🎉 Daily Automation Completed Successfully");
        heading.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #34d399;");

        Label l1 = new Label(String.format("• Processed %d candidates → Shortlisted top %d applicants into MaxHeap",
                result.totalCandidatesProcessed, result.shortlistedCount));
        Label l2 = new Label(String.format("• Allocated %d collision-free interview sessions using Graph Vertex Coloring",
                result.scheduledInterviewsCount));
        Label l3 = new Label(String.format("• Sweep-Line detector identified %d departmental absence overlaps",
                result.leaveConflictsDetected));
        Label l4 = new Label(String.format("• Created & queued %d high-priority HR operational tasks into MaxHeap queue",
                result.urgentTasksCreated));

        for (Label l : List.of(l1, l2, l3, l4)) {
            l.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 13px;");
        }

        summaryBox.getChildren().addAll(heading, l1, l2, l3, l4);
        logContainer.getChildren().add(summaryBox);
    }
}
