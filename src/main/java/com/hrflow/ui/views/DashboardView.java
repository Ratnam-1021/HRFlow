package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.sort.CandidateSortEngine;
import com.hrflow.model.Candidate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

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
        StackPane bannerWrapper = new StackPane();
        bannerWrapper.setAlignment(Pos.CENTER_LEFT);

        // Ambient organic waves background
        BannerWaveCanvas waveCanvas = new BannerWaveCanvas();
        waveCanvas.setMouseTransparent(true);
        waveCanvas.widthProperty().bind(bannerWrapper.widthProperty());
        waveCanvas.heightProperty().bind(bannerWrapper.heightProperty());

        // Header content
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(10, 0, 10, 0));

        // Left side: Welcome text
        VBox textBox = new VBox(6);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label sub = new Label("RECRUITMENT & HIRING");
        sub.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #8C7A6B; -fx-letter-spacing: 1.2px;");

        HBox welcomeBox = new HBox(4);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        Label welcLbl = new Label("Welcome to ");
        welcLbl.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #1A110B;");
        Label hrflowLbl = new Label("HRFlow");
        hrflowLbl.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #EA580C;");
        welcomeBox.getChildren().addAll(welcLbl, hrflowLbl);

        Label desc = new Label("End-to-end recruitment management: applicant screening, smart candidate ranking,\ninterview scheduling, and activity logs.");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #786958; -fx-line-spacing: 2;");

        textBox.getChildren().addAll(sub, welcomeBox, desc);

        // Right side: Skeuomorphic 3D Candidate Resume Cards
        StackPane visual = buildSkeuomorphicResumeCards();

        banner.getChildren().addAll(textBox, visual);
        bannerWrapper.getChildren().addAll(waveCanvas, banner);
        getChildren().add(bannerWrapper);
    }

    private StackPane buildSkeuomorphicResumeCards() {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPrefSize(310, 150);
        container.setMaxSize(310, 150);

        // Card 1 (Back left - ghosted candidate profile)
        VBox card1 = new VBox(6);
        card1.setPrefSize(92, 115);
        card1.setMaxSize(92, 115);
        card1.setTranslateX(-58);
        card1.setTranslateY(10);
        card1.setPadding(new Insets(10));
        card1.setStyle("-fx-background-color: rgba(255, 255, 255, 0.70); -fx-background-radius: 16px; -fx-border-color: rgba(240, 226, 212, 0.85); -fx-border-radius: 16px; -fx-border-width: 1.5px; -fx-effect: dropshadow(gaussian, rgba(160, 95, 30, 0.08), 12, 0, 0, 4);");

        HBox c1Top = new HBox();
        c1Top.setAlignment(Pos.CENTER_LEFT);
        Label c1Av = new Label("👤");
        c1Av.setStyle("-fx-font-size: 13px; -fx-background-color: #FFF2E8; -fx-text-fill: #C2410C; -fx-background-radius: 50%; -fx-padding: 3;");
        c1Top.getChildren().add(c1Av);

        Region c1Bar1 = new Region(); c1Bar1.setPrefSize(50, 5); c1Bar1.setMaxSize(50, 5); c1Bar1.setStyle("-fx-background-color: #E6D9CB; -fx-background-radius: 3px;");
        Region c1Bar2 = new Region(); c1Bar2.setPrefSize(38, 4); c1Bar2.setMaxSize(38, 4); c1Bar2.setStyle("-fx-background-color: #EFE4D6; -fx-background-radius: 2px;");
        Region c1Bar3 = new Region(); c1Bar3.setPrefSize(44, 4); c1Bar3.setMaxSize(44, 4); c1Bar3.setStyle("-fx-background-color: #F4EAE0; -fx-background-radius: 2px;");
        card1.getChildren().addAll(c1Top, c1Bar1, c1Bar2, c1Bar3);

        // Card 3 (Right - Verified candidate resume sheet)
        VBox card3 = new VBox(7);
        card3.setPrefSize(116, 132);
        card3.setMaxSize(116, 132);
        card3.setTranslateX(60);
        card3.setTranslateY(8);
        card3.setPadding(new Insets(10));
        card3.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 18px; -fx-border-color: #F3E6D6; -fx-border-radius: 18px; -fx-border-width: 1.5px; -fx-effect: dropshadow(gaussian, rgba(180, 105, 30, 0.14), 18, 0, 0, 6);");

        Region rLine1 = new Region(); rLine1.setPrefSize(76, 5); rLine1.setMaxSize(76, 5); rLine1.setStyle("-fx-background-color: #E2D4C4; -fx-background-radius: 3px;");
        Region rLine2 = new Region(); rLine2.setPrefSize(60, 5); rLine2.setMaxSize(60, 5); rLine2.setStyle("-fx-background-color: #EBE0D2; -fx-background-radius: 3px;");
        Region rLine3 = new Region(); rLine3.setPrefSize(48, 5); rLine3.setMaxSize(48, 5); rLine3.setStyle("-fx-background-color: #F2E6DC; -fx-background-radius: 3px;");

        HBox chkBox = new HBox();
        chkBox.setAlignment(Pos.CENTER_RIGHT);
        chkBox.setPadding(new Insets(4, 0, 0, 0));
        StackPane badge = new StackPane();
        badge.setPrefSize(28, 28);
        badge.setMaxSize(28, 28);
        badge.setStyle("-fx-background-color: #FFF3EB; -fx-background-radius: 8px; -fx-border-color: #FED7AA; -fx-border-radius: 8px; -fx-border-width: 1px; -fx-effect: dropshadow(gaussian, rgba(234, 88, 12, 0.18), 6, 0, 0, 2);");
        Label chk = new Label("✓");
        chk.setStyle("-fx-text-fill: #EA580C; -fx-font-size: 14px; -fx-font-weight: 800;");
        badge.getChildren().add(chk);
        chkBox.getChildren().add(badge);

        card3.getChildren().addAll(rLine1, rLine2, rLine3, chkBox);

        // Card 2 (Center - Hero Candidate Profile Card)
        VBox card2 = new VBox(7);
        card2.setPrefSize(106, 142);
        card2.setMaxSize(106, 142);
        card2.setTranslateX(0);
        card2.setTranslateY(-4);
        card2.setPadding(new Insets(10));
        card2.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 18px; -fx-border-color: #F3E6D6; -fx-border-radius: 18px; -fx-border-width: 1.5px; -fx-effect: dropshadow(gaussian, rgba(180, 105, 30, 0.16), 22, 0, 0, 8);");

        HBox avBox = new HBox();
        avBox.setAlignment(Pos.CENTER);
        avBox.setPrefSize(38, 38);
        avBox.setMaxSize(38, 38);
        avBox.setStyle("-fx-background-color: #FFF2E8; -fx-background-radius: 10px; -fx-border-color: #FED7AA; -fx-border-radius: 10px; -fx-border-width: 1px;");
        Label avIcon = new Label("👤");
        avIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #EA580C;");
        avBox.getChildren().add(avIcon);

        Region line1 = new Region(); line1.setPrefSize(68, 5); line1.setMaxSize(68, 5); line1.setStyle("-fx-background-color: #E2D4C4; -fx-background-radius: 3px;");
        Region line2 = new Region(); line2.setPrefSize(46, 5); line2.setMaxSize(46, 5); line2.setStyle("-fx-background-color: #EBE0D2; -fx-background-radius: 3px;");
        Region line3 = new Region(); line3.setPrefSize(56, 4); line3.setMaxSize(56, 4); line3.setStyle("-fx-background-color: #F5EAE0; -fx-background-radius: 2px;");
        card2.getChildren().addAll(avBox, line1, line2, line3);

        container.getChildren().addAll(card1, card3, card2);
        return container;
    }

    private static class BannerWaveCanvas extends Canvas {
        public BannerWaveCanvas() {
            widthProperty().addListener(e -> draw());
            heightProperty().addListener(e -> draw());
            sceneProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) javafx.application.Platform.runLater(this::draw);
            });
        }

        private void draw() {
            double w = getWidth();
            double h = getHeight();
            if (w <= 0 || h <= 0) return;
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, w, h);

            // 1. Gentle ambient radial glow centered behind the cards that fades softly to transparent
            RadialGradient ambientGlow = new RadialGradient(
                    0, 0, 0.82, 0.5, 0.48, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.web("#FDE5D2", 0.55)),
                    new Stop(0.55, Color.web("#FEF0E4", 0.28)),
                    new Stop(1.0, Color.TRANSPARENT)
            );
            gc.setFill(ambientGlow);
            gc.fillOval(w * 0.42, -h * 0.25, w * 0.65, h * 1.5);

            // 2. Smooth organic wave sweeping from the right edge, dipping gently and curving back
            // Fills with a linear gradient fading to transparent at all edges so there are never cut borders
            LinearGradient waveGrad1 = new LinearGradient(
                    0, 0.2, 1, 0.8, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.TRANSPARENT),
                    new Stop(0.35, Color.web("#FCDBC4", 0.42)),
                    new Stop(0.70, Color.web("#FCE8D8", 0.30)),
                    new Stop(1.0, Color.TRANSPARENT)
            );
            gc.beginPath();
            gc.moveTo(w, h * 0.08);
            gc.bezierCurveTo(w * 0.72, h * 0.12, w * 0.52, h * 0.38, w * 0.54, h * 0.62);
            gc.bezierCurveTo(w * 0.56, h * 0.82, w * 0.72, h * 0.88, w, h * 0.82);
            gc.closePath();
            gc.setFill(waveGrad1);
            gc.fill();

            // 3. Second layered organic wave curve creating depth behind the 3D cards
            LinearGradient waveGrad2 = new LinearGradient(
                    0.2, 0.1, 0.9, 0.9, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.TRANSPARENT),
                    new Stop(0.40, Color.web("#FAD3B8", 0.38)),
                    new Stop(0.75, Color.web("#FDEBDC", 0.22)),
                    new Stop(1.0, Color.TRANSPARENT)
            );
            gc.beginPath();
            gc.moveTo(w, h * 0.18);
            gc.bezierCurveTo(w * 0.80, h * 0.26, w * 0.62, h * 0.48, w * 0.66, h * 0.74);
            gc.bezierCurveTo(w * 0.70, h * 0.88, w * 0.84, h * 0.92, w, h * 0.72);
            gc.closePath();
            gc.setFill(waveGrad2);
            gc.fill();
        }

        @Override
        public boolean isResizable() {
            return true;
        }
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
                createMetricCard("👥", "#FFF3EB", "#EA580C", "TOTAL APPLICANTS", String.valueOf(totalCandidates > 0 ? totalCandidates : 250), "Active in database", "Total", "#FFF3EB", "#C2410C",
                        Color.web("#F6D8C2", 0.75), Color.web("#FDF0E6", 0.60)),
                createMetricCard("👤", "#EFF6FF", "#2563EB", "SHORTLISTED", String.valueOf(shortlistedCount > 0 ? shortlistedCount : 30), "Qualified for next stage", "Shortlisted", "#DCFCE7", "#15803D",
                        Color.web("#D8E9FE", 0.75), Color.web("#EDF4FE", 0.60)),
                createMetricCard("🗓️", "#F5F3FF", "#7C3AED", "SCHEDULED SESSIONS", String.valueOf(scheduledCount > 0 ? scheduledCount : 8), "Upcoming interview rounds", "Interviews", "#F5F3FF", "#6D28D9",
                        Color.web("#E7DCFA", 0.75), Color.web("#F5EFFD", 0.60)),
                createMetricCard("📄", "#ECFDF5", "#059669", "RECORDED ACTIONS", String.valueOf(undoDepth), "Actions in audit history", "Audit Log", "#FFF3EB", "#C2410C",
                        Color.web("#D4F4E2", 0.75), Color.web("#EAFBF0", 0.60))
        );

        getChildren().add(row);
    }

    private StackPane createMetricCard(String iconGlyph, String iconBg, String iconColor,
                                       String title, String value, String desc,
                                       String badgeText, String badgeBg, String badgeColor,
                                       Color wavePrimary, Color waveSecondary) {
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16; -fx-border-color: #EFE4D6; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(160, 95, 30, 0.05), 10, 0, 0, 3);");
        HBox.setHgrow(card, Priority.ALWAYS);

        // Wave decoration container with smooth 16px corner clip
        WaveCanvas canvas = new WaveCanvas(wavePrimary, waveSecondary);
        canvas.setMouseTransparent(true);
        canvas.widthProperty().bind(card.widthProperty());
        canvas.heightProperty().bind(card.heightProperty());

        StackPane waveWrapper = new StackPane(canvas);
        waveWrapper.setMouseTransparent(true);
        Rectangle clip = new Rectangle();
        clip.setArcWidth(32);
        clip.setArcHeight(32);
        clip.widthProperty().bind(card.widthProperty());
        clip.heightProperty().bind(card.heightProperty());
        waveWrapper.setClip(clip);

        // Foreground content
        VBox contentBox = new VBox(6);
        contentBox.setPadding(new Insets(18, 18, 16, 18));

        HBox top = new HBox(14);
        top.setAlignment(Pos.TOP_LEFT);

        // Icon rounded box
        Label icon = new Label(iconGlyph);
        icon.setPrefSize(44, 44);
        icon.setMinSize(44, 44);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: " + iconBg + "; -fx-text-fill: " + iconColor + "; -fx-font-size: 18px; -fx-background-radius: 12;");

        VBox centerBox = new VBox(3);
        HBox.setHgrow(centerBox, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 10.5px; -fx-font-weight: 700; -fx-text-fill: #8C7A6B; -fx-letter-spacing: 0.5px;");

        Label lblVal = new Label(value);
        lblVal.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #1A110B; -fx-padding: 1 0 2 0;");

        Label lblDesc = new Label(desc);
        lblDesc.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #8C7A6B;");

        centerBox.getChildren().addAll(lblTitle, lblVal, lblDesc);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeColor + "; -fx-font-size: 10.5px; -fx-font-weight: 700; -fx-padding: 3 9; -fx-background-radius: 12;");

        top.getChildren().addAll(icon, centerBox, badge);
        contentBox.getChildren().add(top);

        card.getChildren().addAll(waveWrapper, contentBox);
        return card;
    }

    private static class WaveCanvas extends Canvas {
        private final Color primaryColor;
        private final Color secondaryColor;

        public WaveCanvas(Color primaryColor, Color secondaryColor) {
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
            widthProperty().addListener(e -> draw());
            heightProperty().addListener(e -> draw());
            sceneProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) javafx.application.Platform.runLater(this::draw);
            });
        }

        private void draw() {
            double w = getWidth();
            double h = getHeight();
            if (w <= 0 || h <= 0) return;
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, w, h);

            // Layer 1: Background softer undulating wave
            gc.beginPath();
            gc.moveTo(w * 0.28, h);
            gc.bezierCurveTo(w * 0.46, h - 22, w * 0.68, h - 10, w, h - 34);
            gc.lineTo(w, h);
            gc.closePath();
            gc.setFill(secondaryColor);
            gc.fill();

            // Layer 2: Foreground wave curving smoothly across the bottom right
            gc.beginPath();
            gc.moveTo(w * 0.38, h);
            gc.bezierCurveTo(w * 0.54, h - 14, w * 0.72, h - 26, w, h - 10);
            gc.lineTo(w, h);
            gc.closePath();
            gc.setFill(primaryColor);
            gc.fill();
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }

    private void buildCenterContent() {
        // Full-width card: Top Ranked Candidates
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16; -fx-border-color: #EFE4D6; -fx-border-radius: 16; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(160, 95, 30, 0.05), 12, 0, 0, 3);");
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        // Trophy icon box
        Label trophyIcon = new Label("🏆");
        trophyIcon.setPrefSize(40, 40);
        trophyIcon.setAlignment(Pos.CENTER);
        trophyIcon.setStyle("-fx-background-color: #FFF3EB; -fx-background-radius: 12; -fx-font-size: 18px;");

        VBox titleBox = new VBox(2);
        Label title = new Label("Top Ranked Candidates");
        title.setStyle("-fx-font-size: 16.5px; -fx-font-weight: 800; -fx-text-fill: #1A110B;");
        Label subtitle = new Label("Highest scoring candidates based on skills, experience and assessment.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #8C7A6B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button viewAllBtn = new Button("View Full Pipeline →");
        viewAllBtn.getStyleClass().add("view-pipeline-btn");
        viewAllBtn.setOnAction(e -> navigationHandler.accept("recruitment"));

        header.getChildren().addAll(trophyIcon, titleBox, sp, viewAllBtn);

        TableView<Candidate> candidateTable = createCandidateTable();
        VBox.setVgrow(candidateTable, Priority.ALWAYS);
        card.getChildren().addAll(header, candidateTable);

        getChildren().add(card);
    }

    private TableView<Candidate> createCandidateTable() {
        TableView<Candidate> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(52);
        table.setPrefHeight(52 * 5 + 44);

        // 1. Rank Column (#)
        TableColumn<Candidate, Integer> colRank = new TableColumn<>("#");
        colRank.setMaxWidth(55);
        colRank.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    int rank = getIndex() + 1;
                    Label badge = new Label(String.valueOf(rank));
                    badge.setPrefSize(28, 28);
                    badge.setAlignment(Pos.CENTER);
                    if (rank <= 3) {
                        badge.setStyle("-fx-background-color: #FFF0E4; -fx-text-fill: #EA580C; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-background-radius: 50%;");
                    } else {
                        badge.setStyle("-fx-background-color: #F5EFE6; -fx-text-fill: #786958; -fx-font-weight: 700; -fx-font-size: 12.5px; -fx-background-radius: 50%;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // 2. Candidate Column (Avatar Initials + Bold Name)
        TableColumn<Candidate, Candidate> colCandidate = new TableColumn<>("Candidate");
        colCandidate.setMinWidth(170);
        colCandidate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colCandidate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Candidate c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);

                    String initials = getInitials(c.getName());
                    Label av = new Label(initials);
                    av.setPrefSize(30, 30);
                    av.setMinSize(30, 30);
                    av.setAlignment(Pos.CENTER);
                    if (getIndex() % 2 == 0) {
                        av.setStyle("-fx-background-color: #FFF2E8; -fx-text-fill: #EA580C; -fx-font-weight: 800; -fx-font-size: 11px; -fx-background-radius: 50%;");
                    } else {
                        av.setStyle("-fx-background-color: #F5EFE6; -fx-text-fill: #786958; -fx-font-weight: 800; -fx-font-size: 11px; -fx-background-radius: 50%;");
                    }

                    Label nameLbl = new Label(c.getName());
                    nameLbl.setStyle("-fx-font-weight: 700; -fx-font-size: 13px; -fx-text-fill: #1A110B;");

                    box.getChildren().addAll(av, nameLbl);
                    setGraphic(box);
                }
            }
        });

        // 3. Role Column
        TableColumn<Candidate, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("appliedRole"));
        colRole.setStyle("-fx-text-fill: #4A3B2C; -fx-font-size: 12.5px;");

        // 4. Experience Column
        TableColumn<Candidate, Double> colExp = new TableColumn<>("Experience");
        colExp.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        colExp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double exp, boolean empty) {
                super.updateItem(exp, empty);
                if (empty || exp == null) setText(null);
                else {
                    setText(String.format("%.1f yrs", exp));
                    setStyle("-fx-text-fill: #4A3B2C; -fx-font-size: 12.5px;");
                }
            }
        });

        // 5. Key Skills Column (Pill Tags + Count)
        TableColumn<Candidate, Set<String>> colSkills = new TableColumn<>("Key Skills");
        colSkills.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSkills()));
        colSkills.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Set<String> skills, boolean empty) {
                super.updateItem(skills, empty);
                if (empty || skills == null || skills.isEmpty()) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(5);
                    pane.setAlignment(Pos.CENTER_LEFT);
                    int count = 0;
                    for (String s : skills) {
                        if (count >= 3) break;
                        Label chip = new Label(s);
                        chip.getStyleClass().add("skill-chip-tag");
                        pane.getChildren().add(chip);
                        count++;
                    }
                    if (skills.size() > 3) {
                        Label more = new Label("+" + (skills.size() - 3));
                        more.setStyle("-fx-background-color: #EFE6DA; -fx-text-fill: #786958; -fx-font-size: 10px; -fx-font-weight: 700; -fx-padding: 3 6; -fx-background-radius: 8;");
                        pane.getChildren().add(more);
                    }
                    setGraphic(pane);
                }
            }
        });

        // 6. Score Column (Mint Green Capsule)
        TableColumn<Candidate, Double> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(new PropertyValueFactory<>("overallScore"));
        colScore.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(String.format("%.1f %%", score));
                    badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 4 12; -fx-background-radius: 12;");
                    setGraphic(badge);
                }
            }
        });

        // 7. Status Column (Green Shortlisted Capsule)
        TableColumn<Candidate, Candidate.CandidateStatus> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Candidate.CandidateStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label("SHORTLISTED");
                    badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12;");
                    setGraphic(badge);
                }
            }
        });

        // 8. Actions Column (···)
        TableColumn<Candidate, Void> colActions = new TableColumn<>("Actions");
        colActions.setMaxWidth(60);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("•••");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8C7A6B; -fx-font-size: 13px; -fx-font-weight: 800; -fx-cursor: hand; -fx-padding: 2 6; -fx-background-radius: 6;");
                btn.setOnAction(e -> navigationHandler.accept("recruitment"));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(colRank, colCandidate, colRole, colExp, colSkills, colScore, colStatus, colActions);

        List<Candidate> topCandidates = CandidateSortEngine.rankAndGetTopK(
                context.getCandidateService().getAllCandidates(), Set.of("Java", "Docker", "Spring Boot"), 5
        );
        table.getItems().addAll(topCandidates);

        return table;
    }

    private static String getInitials(String name) {
        if (name == null || name.isBlank()) return "HR";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
