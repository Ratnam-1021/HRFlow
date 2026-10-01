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

    public MainLayout() {
        setTop(buildTopNavBar());
        setCenter(contentArea);

        switchView("dashboard");
    }

    private HBox buildTopNavBar() {
        HBox navBar = new HBox(20);
        navBar.getStyleClass().add("top-navbar");
        navBar.setAlignment(Pos.CENTER_LEFT);

        // Brand on the left
        VBox brandBox = new VBox(1);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label logo = new Label("HRFlow");
        logo.getStyleClass().add("logo-text");
        Label sub = new Label("RECRUITMENT & HIRING");
        sub.getStyleClass().add("logo-subtext");
        brandBox.getChildren().addAll(logo, sub);

        // Vertical divider
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setPrefHeight(26);
        sep.setStyle("-fx-background-color: #EFE4D6;");

        // Horizontal navigation tabs with rounded rectangle cards
        HBox navButtonsBox = new HBox(10);
        navButtonsBox.setAlignment(Pos.CENTER_LEFT);
        addNavItem(navButtonsBox, "dashboard", "📊  Dashboard");
        addNavItem(navButtonsBox, "recruitment", "👥  Candidate Pipeline");
        addNavItem(navButtonsBox, "scheduling", "🗓️  Interview Scheduler");
        addNavItem(navButtonsBox, "history", "📜  Activity & Audit Log");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navBar.getChildren().addAll(brandBox, sep, navButtonsBox, spacer);
        return navBar;
    }

    private void addNavItem(HBox container, String key, String label) {
        Button btn = new Button(label);
        btn.getStyleClass().add("nav-card-button");
        btn.setOnAction(e -> switchView(key));
        navButtons.put(key, btn);
        container.getChildren().add(btn);
    }

    public void switchView(String key) {
        navButtons.values().forEach(b -> b.getStyleClass().remove("nav-card-button-active"));
        Button activeBtn = navButtons.get(key);
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("nav-card-button-active");
        }

        Node view = viewCache.computeIfAbsent(key, this::createViewInstance);

        if (key.equals("history") && view instanceof HistoryView hv) {
            hv.refreshHistory();
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
