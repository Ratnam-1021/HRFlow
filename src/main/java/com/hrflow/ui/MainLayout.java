package com.hrflow.ui;

import com.hrflow.ui.views.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

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

        // Real HRFlow Brand Icon
        ImageView logoIcon = new ImageView();
        try {
            var is = getClass().getResourceAsStream("/com/hrflow/images/app_icon_orange.png");
            if (is != null) {
                Image img = new Image(is, 34, 34, true, true);
                logoIcon.setImage(img);
                Rectangle clip = new Rectangle(34, 34);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                logoIcon.setClip(clip);
            }
        } catch (Exception ignored) {}

        // Brand on the left
        VBox brandBox = new VBox(0);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        HBox brandTitleBox = new HBox(0);
        Label hrLbl = new Label("HR");
        hrLbl.setStyle("-fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #1A110B;");
        Label flowLbl = new Label("Flow");
        flowLbl.setStyle("-fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #EA580C;");
        brandTitleBox.getChildren().addAll(hrLbl, flowLbl);

        // Left side: Brand with logo
        HBox leftBox = new HBox(10);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getChildren().addAll(logoIcon, brandBox);

        // Center: Horizontal navigation tabs without dropdown chevrons
        HBox navButtonsBox = new HBox(8);
        navButtonsBox.setAlignment(Pos.CENTER);
        addNavItem(navButtonsBox, "dashboard", "📊 Dashboard");
        addNavItem(navButtonsBox, "recruitment", "👥 Candidate Pipeline");
        addNavItem(navButtonsBox, "scheduling", "🗓️ Interview Scheduler");
        addNavItem(navButtonsBox, "history", "📄 Activity & Audit Log");

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Right side: Profile button without dropdown chevron
        HBox rightCluster = new HBox();
        rightCluster.setAlignment(Pos.CENTER_RIGHT);

        HBox userBtn = new HBox(6);
        userBtn.getStyleClass().add("dark-action-btn");
        userBtn.setAlignment(Pos.CENTER);
        Label avatar = new Label("RS");
        avatar.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-background-color: #334155; -fx-text-fill: #FFFFFF; -fx-padding: 1 5; -fx-background-radius: 4;");
        Label userName = new Label("Ratnam Singh");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #FFFFFF;");
        userBtn.getChildren().addAll(avatar, userName);

        rightCluster.getChildren().add(userBtn);

        navBar.getChildren().addAll(leftBox, leftSpacer, navButtonsBox, rightSpacer, rightCluster);
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
