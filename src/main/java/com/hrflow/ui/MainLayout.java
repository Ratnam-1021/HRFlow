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
        HBox navBar = new HBox(16);
        navBar.getStyleClass().add("top-navbar");
        navBar.setAlignment(Pos.CENTER_LEFT);

        // Logo icon box
        HBox logoIcon = new HBox(2.5);
        logoIcon.getStyleClass().add("logo-icon-box");
        logoIcon.setAlignment(Pos.BOTTOM_CENTER);
        logoIcon.setPrefSize(32, 32);
        Region b1 = new Region(); b1.setPrefSize(3.5, 10); b1.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 2;");
        Region b2 = new Region(); b2.setPrefSize(3.5, 16); b2.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 2;");
        Region b3 = new Region(); b3.setPrefSize(3.5, 12); b3.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 2;");
        logoIcon.getChildren().addAll(b1, b2, b3);

        // Brand on the left
        VBox brandBox = new VBox(0);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        HBox brandTitleBox = new HBox(0);
        Label hrLbl = new Label("HR");
        hrLbl.setStyle("-fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #1A110B;");
        Label flowLbl = new Label("Flow");
        flowLbl.setStyle("-fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #EA580C;");
        brandTitleBox.getChildren().addAll(hrLbl, flowLbl);

        Label sub = new Label("RECRUITMENT & HIRING");
        sub.getStyleClass().add("logo-subtext");
        brandBox.getChildren().addAll(brandTitleBox, sub);

        // Vertical divider
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setPrefHeight(22);
        sep.setStyle("-fx-background-color: #EFE4D6;");

        // Horizontal navigation tabs with dropdown indicators
        HBox navButtonsBox = new HBox(8);
        navButtonsBox.setAlignment(Pos.CENTER_LEFT);
        addNavItem(navButtonsBox, "dashboard", "📊 Dashboard ▾");
        addNavItem(navButtonsBox, "recruitment", "👥 Candidate Pipeline ▾");
        addNavItem(navButtonsBox, "scheduling", "🗓️ Interview Scheduler ▾");
        addNavItem(navButtonsBox, "history", "📄 Activity & Audit Log ▾");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side: Icons + Profile button matching the hanging banner mockup
        HBox rightCluster = new HBox(8);
        rightCluster.setAlignment(Pos.CENTER_RIGHT);

        // Language / Translate icon button
        StackPane langBtn = new StackPane();
        langBtn.getStyleClass().add("icon-circle-btn");
        Label langLbl = new Label("文A");
        langLbl.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700; -fx-text-fill: #5C4D3E;");
        langBtn.getChildren().add(langLbl);

        // Dark mode moon button with red alert dot
        StackPane moonBtn = new StackPane();
        moonBtn.getStyleClass().add("icon-circle-btn");
        Label moon = new Label("🌙");
        moon.setStyle("-fx-font-size: 13px;");
        Region moonDot = new Region();
        moonDot.setPrefSize(6, 6);
        moonDot.setMaxSize(6, 6);
        moonDot.setStyle("-fx-background-color: #EF4444; -fx-background-radius: 50%;");
        StackPane.setAlignment(moonDot, Pos.TOP_RIGHT);
        StackPane.setMargin(moonDot, new Insets(6, 6, 0, 0));
        moonBtn.getChildren().addAll(moon, moonDot);

        // Notification bell
        StackPane notifBtn = new StackPane();
        notifBtn.getStyleClass().add("icon-circle-btn");
        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 13px;");
        notifBtn.getChildren().add(bell);

        // Dark action button (matches the Sign In button style in reference banner)
        HBox userBtn = new HBox(6);
        userBtn.getStyleClass().add("dark-action-btn");
        userBtn.setAlignment(Pos.CENTER);
        Label avatar = new Label("RS");
        avatar.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-background-color: #334155; -fx-text-fill: #FFFFFF; -fx-padding: 1 5; -fx-background-radius: 4;");
        Label userName = new Label("Ratnam Singh  ▾");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #FFFFFF;");
        userBtn.getChildren().addAll(avatar, userName);

        rightCluster.getChildren().addAll(langBtn, moonBtn, notifBtn, userBtn);

        navBar.getChildren().addAll(logoIcon, brandBox, sep, navButtonsBox, spacer, rightCluster);
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
