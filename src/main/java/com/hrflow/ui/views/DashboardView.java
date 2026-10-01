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
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(8, 0, 10, 0));

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

        // Right side: Visual 3D floating card graphic
        StackPane visual = new StackPane();
        visual.setAlignment(Pos.CENTER_RIGHT);
        visual.setPrefSize(180, 95);

        VBox cardBack = new VBox();
        cardBack.setPrefSize(110, 80);
        cardBack.setMaxSize(110, 80);
        cardBack.setStyle("-fx-background-color: rgba(255, 245, 235, 0.7); -fx-background-radius: 14; -fx-border-color: rgba(239, 228, 214, 0.8); -fx-border-radius: 14;");
        cardBack.setRotate(8);

        VBox cardFront = new VBox(6);
        cardFront.setPrefSize(115, 85);
        cardFront.setMaxSize(115, 85);
        cardFront.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; -fx-border-color: #EFE4D6; -fx-border-radius: 14; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(160, 95, 30, 0.08), 10, 0, 0, 3);");

        HBox topRow = new HBox(6);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label iconUser = new Label("👤");
        iconUser.setStyle("-fx-font-size: 13px; -fx-background-color: #FFF2E8; -fx-background-radius: 50%; -fx-padding: 3;");
        Region barA = new Region(); barA.setPrefSize(45, 4); barA.setStyle("-fx-background-color: #F0E6D8; -fx-background-radius: 2;");
        topRow.getChildren().addAll(iconUser, barA);

        Region barB = new Region(); barB.setPrefSize(80, 4); barB.setStyle("-fx-background-color: #F4EAE0; -fx-background-radius: 2;");
        Region barC = new Region(); barC.setPrefSize(60, 4); barC.setStyle("-fx-background-color: #F4EAE0; -fx-background-radius: 2;");

        HBox botRow = new HBox();
        botRow.setAlignment(Pos.CENTER_RIGHT);
        Label check = new Label("✓");
        check.setStyle("-fx-background-color: #FFF2E8; -fx-text-fill: #EA580C; -fx-font-weight: 800; -fx-font-size: 10px; -fx-background-radius: 50%; -fx-padding: 1 5;");
        botRow.getChildren().add(check);

        cardFront.getChildren().addAll(topRow, barB, barC, botRow);
        visual.getChildren().addAll(cardBack, cardFront);

        banner.getChildren().addAll(textBox, visual);
        getChildren().add(banner);
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
                createMetricCard("👥", "#FFF3EB", "#EA580C", "TOTAL APPLICANTS", String.valueOf(totalCandidates > 0 ? totalCandidates : 250), "Active in database", "Total", "#FFF3EB", "#C2410C"),
                createMetricCard("👤", "#EFF6FF", "#2563EB", "SHORTLISTED", String.valueOf(shortlistedCount > 0 ? shortlistedCount : 30), "Qualified for next stage", "Qualified", "#FFF3EB", "#EA580C"),
                createMetricCard("🗓️", "#F5F3FF", "#7C3AED", "SCHEDULED SESSIONS", String.valueOf(scheduledCount > 0 ? scheduledCount : 8), "Upcoming interview rounds", "Interviews", "#F5F3FF", "#6D28D9"),
                createMetricCard("📄", "#ECFDF5", "#059669", "RECORDED ACTIONS", String.valueOf(undoDepth), "Actions in audit history", "Audit Log", "#FFF3EB", "#C2410C")
        );

        getChildren().add(row);
    }

    private VBox createMetricCard(String iconGlyph, String iconBg, String iconColor,
                                 String title, String value, String desc,
                                 String badgeText, String badgeBg, String badgeColor) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16; -fx-border-color: #EFE4D6; -fx-border-radius: 16; -fx-padding: 18 18 16 18; -fx-effect: dropshadow(gaussian, rgba(160, 95, 30, 0.05), 10, 0, 0, 3);");
        HBox.setHgrow(card, Priority.ALWAYS);

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
        card.getChildren().add(top);
        return card;
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
