package com.hrflow.ui;

import com.hrflow.ui.views.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Main Layout Shell strictly tailored for the Recruitment & Hiring Subsystem.
 */
public class MainLayout extends BorderPane {
    private final StackPane contentArea = new StackPane();
    private final Map<String, Node> viewCache = new HashMap<>();
    private final Map<String, Button> navButtons = new HashMap<>();
    private final Label topTitle = new Label("Recruitment & Hiring Overview");

    public MainLayout() {
        setLeft(buildSidebar());
        setTop(buildTopBar());
        setCenter(contentArea);

        switchView("dashboard");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);
        sidebar.setPadding(new Insets(20, 14, 20, 14));

        // Brand
        VBox brandBox = new VBox(2);
        brandBox.setPadding(new Insets(0, 0, 16, 8));
        Label logo = new Label("HRFlow");
        logo.getStyleClass().add("logo-text");
        Label sub = new Label("RECRUITMENT & HIRING SUBSYSTEM");
        sub.getStyleClass().add("logo-subtext");
        brandBox.getChildren().addAll(logo, sub);

        // Navigation strictly for Recruitment & Hiring
        VBox navList = new VBox(6);
        addNavItem(navList, "dashboard", "📊  Dashboard");
        addNavItem(navList, "recruitment", "👥  Candidate Pipeline");
        addNavItem(navList, "scheduling", "🗓️  Interview Scheduler");
        addNavItem(navList, "history", "📜  Activity & Audit Log");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer status card
        VBox engineInfo = new VBox(4);
        engineInfo.setStyle("-fx-background-color: #FAF5ED; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #EFE4D6; -fx-border-radius: 8;");
        Label eTitle = new Label("System Status");
        eTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #EA580C;");
        Label eDesc = new Label("• Pipeline Active\n• Scheduler Ready\n• Audit Logging Enabled");
        eDesc.setStyle("-fx-font-size: 10.5px; -fx-text-fill: #5C4D3E;");
        engineInfo.getChildren().addAll(eTitle, eDesc);

        sidebar.getChildren().addAll(brandBox, navList, spacer, engineInfo);
        return sidebar;
    }

    private void addNavItem(VBox container, String key, String label) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("nav-button");
        btn.setOnAction(e -> switchView(key));
        navButtons.put(key, btn);
        container.getChildren().add(btn);
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox(16);
        topBar.getStyleClass().add("top-header");
        topBar.setAlignment(Pos.CENTER_LEFT);

        topTitle.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox liveIndicator = new HBox(6);
        liveIndicator.setAlignment(Pos.CENTER);
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #15803D; -fx-font-size: 10px;");
        Label status = new Label("Recruitment Pipeline Online");
        status.setStyle("-fx-text-fill: #5C4D3E; -fx-font-size: 12px; -fx-font-weight: 600;");
        liveIndicator.getChildren().addAll(dot, status);

        topBar.getChildren().addAll(topTitle, spacer, liveIndicator);
        return topBar;
    }

    public void switchView(String key) {
        navButtons.values().forEach(b -> b.getStyleClass().remove("nav-button-active"));
        Button activeBtn = navButtons.get(key);
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("nav-button-active");
        }

        Node view = viewCache.computeIfAbsent(key, this::createViewInstance);

        switch (key) {
            case "dashboard" -> topTitle.setText("Recruitment Overview & Pipeline Metrics");
            case "recruitment" -> topTitle.setText("Candidate Sourcing & Screening");
            case "scheduling" -> topTitle.setText("Interview Scheduling & Calendar");
            case "history" -> {
                topTitle.setText("Activity History & Audit Trail");
                if (view instanceof HistoryView hv) {
                    hv.refreshHistory();
                }
            }
        }

        contentArea.getChildren().setAll(view);
    }

    private Node createViewInstance(String key) {
        return switch (key) {
            case "dashboard" -> new DashboardView(this::switchView);
            case "recruitment" -> new RecruitmentView();
            case "scheduling" -> new SchedulingView();
            case "history" -> new HistoryView();
            default -> new DashboardView(this::switchView);
        };
    }
}
