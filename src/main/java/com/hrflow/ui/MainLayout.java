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
 * Main Layout Shell featuring sidebar navigation and dynamic content switching.
 */
public class MainLayout extends BorderPane {
    private final StackPane contentArea = new StackPane();
    private final Map<String, Node> viewCache = new HashMap<>();
    private final Map<String, Button> navButtons = new HashMap<>();
    private final Label topTitle = new Label("Executive Dashboard");

    public MainLayout() {
        setLeft(buildSidebar());
        setTop(buildTopBar());
        setCenter(contentArea);

        // Preload default view
        switchView("dashboard");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20, 14, 20, 14));

        // Brand
        VBox brandBox = new VBox(2);
        brandBox.setPadding(new Insets(0, 0, 16, 8));
        Label logo = new Label("HRFlow");
        logo.getStyleClass().add("logo-text");
        Label sub = new Label("INTELLIGENT HR AUTOMATION");
        sub.getStyleClass().add("logo-subtext");
        brandBox.getChildren().addAll(logo, sub);

        // Navigation links
        VBox navList = new VBox(6);
        addNavItem(navList, "dashboard", "📊  Dashboard");
        addNavItem(navList, "automation", "🚀  Automation Center");
        addNavItem(navList, "recruitment", "👥  Candidate Pipeline");
        addNavItem(navList, "scheduling", "🗓️  Smart Scheduler");
        addNavItem(navList, "employees", "🏢  Hierarchy & Search");
        addNavItem(navList, "leaves", "🏖️  Leave Bottlenecks");
        addNavItem(navList, "tasks", "📋  Priority Tasks");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer info card
        VBox engineInfo = new VBox(4);
        engineInfo.setStyle("-fx-background-color: #131d33; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #1e293b; -fx-border-radius: 8;");
        Label eTitle = new Label("DSA Engine Core");
        eTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #34d399;");
        Label eDesc = new Label("Heap • Trie • Graph • Tree\nInterval • Binary Search");
        eDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");
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
        dot.setStyle("-fx-text-fill: #10b981; -fx-font-size: 10px;");
        Label status = new Label("Algorithmic Engine Active");
        status.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: 600;");
        liveIndicator.getChildren().addAll(dot, status);

        topBar.getChildren().addAll(topTitle, spacer, liveIndicator);
        return topBar;
    }

    public void switchView(String key) {
        // Highlight active button
        navButtons.values().forEach(b -> b.getStyleClass().remove("nav-button-active"));
        Button activeBtn = navButtons.get(key);
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("nav-button-active");
        }

        // Lazy load views
        Node view = viewCache.computeIfAbsent(key, this::createViewInstance);

        switch (key) {
            case "dashboard" -> topTitle.setText("Executive Overview & Metrics");
            case "automation" -> topTitle.setText("HR Automation Center");
            case "recruitment" -> topTitle.setText("Candidate Screening & MaxHeap Ranking");
            case "scheduling" -> topTitle.setText("Conflict-Free Graph Interview Scheduler");
            case "employees" -> topTitle.setText("Organizational Hierarchy & Binary Search Benchmarks");
            case "leaves" -> topTitle.setText("Sweep-Line Leave Conflict Analysis");
            case "tasks" -> topTitle.setText("Operational Task Priority Queue");
        }

        contentArea.getChildren().setAll(view);
    }

    private Node createViewInstance(String key) {
        return switch (key) {
            case "dashboard" -> new DashboardView(this::switchView);
            case "automation" -> new AutomationCenterView();
            case "recruitment" -> new RecruitmentView();
            case "scheduling" -> new SchedulingView();
            case "employees" -> new EmployeesView();
            case "leaves" -> new LeavesView();
            case "tasks" -> new TasksView();
            default -> new DashboardView(this::switchView);
        };
    }
}
