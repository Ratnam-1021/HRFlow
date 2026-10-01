package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.model.Candidate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.*;

/**
 * Recruitment & Candidate Pipeline View.
 * Uses Trie for Skill Autocomplete & inverted search, and MaxHeap for scoring and top-K extraction.
 */
public class RecruitmentView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<Candidate> candidateData = FXCollections.observableArrayList();
    private final TableView<Candidate> table = new TableView<>();
    private final TextField skillSearchField = new TextField();
    private final FlowPane suggestionsPane = new FlowPane();
    private final FlowPane activeSkillsFilter = new FlowPane();
    private final Set<String> selectedFilterSkills = new LinkedHashSet<>();
    private final Spinner<Integer> topKSpinner = new Spinner<>(5, 100, 20, 5);

    public RecruitmentView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildControlBar();
        buildTable();

        loadAllCandidates();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Candidate Pipeline & Algorithmic Screening");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Screen 250+ resumes with Trie prefix indexing and rank candidates with MaxHeap priority queues.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        VBox controlCard = new VBox(12);
        controlCard.getStyleClass().add("glass-card");

        HBox row1 = new HBox(16);
        row1.setAlignment(Pos.CENTER_LEFT);

        VBox searchBox = new VBox(4);
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        Label searchLabel = new Label("Trie Skill Search & Autocomplete:");
        searchLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #cbd5e1;");

        skillSearchField.setPromptText("Type a skill (e.g. Java, Python, React, SQL, AWS)...");
        skillSearchField.getStyleClass().add("search-field");
        skillSearchField.textProperty().addListener((obs, oldVal, newVal) -> updateTrieSuggestions(newVal));

        searchBox.getChildren().addAll(searchLabel, skillSearchField);

        VBox spinnerBox = new VBox(4);
        Label spinnerLabel = new Label("Max-Heap Top K:");
        spinnerLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #cbd5e1;");
        topKSpinner.setStyle("-fx-background-color: #1e293b;");
        topKSpinner.setPrefWidth(90);
        spinnerBox.getChildren().addAll(spinnerLabel, topKSpinner);

        Button rankBtn = new Button("⚡ Rank Candidates via MaxHeap");
        rankBtn.getStyleClass().add("btn-primary");
        rankBtn.setStyle("-fx-padding: 10 18;");
        rankBtn.setOnAction(e -> runMaxHeapRanking());

        Button resetBtn = new Button("Reset All");
        resetBtn.getStyleClass().add("btn-secondary");
        resetBtn.setOnAction(e -> resetFilters());

        row1.getChildren().addAll(searchBox, spinnerBox, rankBtn, resetBtn);

        // Autocomplete suggestions chip bar
        suggestionsPane.setHgap(8);
        suggestionsPane.setVgap(8);

        // Active filtered skills chip bar
        activeSkillsFilter.setHgap(8);
        activeSkillsFilter.setVgap(8);

        controlCard.getChildren().addAll(row1, suggestionsPane, activeSkillsFilter);
        getChildren().add(controlCard);
    }

    private void updateTrieSuggestions(String query) {
        suggestionsPane.getChildren().clear();
        if (query == null || query.trim().length() < 1) {
            return;
        }

        List<String> suggestions = context.getCandidateService().autocompleteSkills(query, 6);
        if (suggestions.isEmpty()) {
            Label noMatch = new Label("No matching skill in Trie");
            noMatch.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px; -fx-font-style: italic;");
            suggestionsPane.getChildren().add(noMatch);
            return;
        }

        for (String skill : suggestions) {
            Button chip = new Button("+ " + skill);
            chip.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #818cf8; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-size: 11.5px; -fx-cursor: hand;");
            chip.setOnAction(e -> addFilterSkill(skill));
            suggestionsPane.getChildren().add(chip);
        }
    }

    private void addFilterSkill(String skill) {
        if (selectedFilterSkills.add(skill)) {
            renderFilterChips();
            runMaxHeapRanking();
        }
    }

    private void removeFilterSkill(String skill) {
        selectedFilterSkills.remove(skill);
        renderFilterChips();
        runMaxHeapRanking();
    }

    private void renderFilterChips() {
        activeSkillsFilter.getChildren().clear();
        for (String skill : selectedFilterSkills) {
            HBox chip = new HBox(6);
            chip.setAlignment(Pos.CENTER);
            chip.setStyle("-fx-background-color: #312e81; -fx-background-radius: 12; -fx-padding: 3 8;");
            Label label = new Label(skill);
            label.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11.5px; -fx-font-weight: 600;");
            Button remove = new Button("×");
            remove.setStyle("-fx-background-color: transparent; -fx-text-fill: #f43f5e; -fx-padding: 0; -fx-font-weight: bold; -fx-cursor: hand;");
            remove.setOnAction(e -> removeFilterSkill(skill));
            chip.getChildren().addAll(label, remove);
            activeSkillsFilter.getChildren().add(chip);
        }
    }

    private void buildTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Candidate, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(90);

        TableColumn<Candidate, String> colName = new TableColumn<>("Candidate Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Candidate, String> colRole = new TableColumn<>("Applied Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("appliedRole"));

        TableColumn<Candidate, Double> colScore = new TableColumn<>("Score (MaxHeap)");
        colScore.setCellValueFactory(new PropertyValueFactory<>("overallScore"));
        colScore.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f %%", score));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                            (score >= 85 ? "#34d399" : score >= 70 ? "#38bdf8" : "#fbbf24"));
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

        TableColumn<Candidate, String> colSkills = new TableColumn<>("Skills Tags");
        colSkills.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.join(", ", cellData.getValue().getSkills())));

        TableColumn<Candidate, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().name()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 10; " +
                            (status.contains("SHORTLIST") ? "-fx-background-color: rgba(16,185,129,0.2); -fx-text-fill: #34d399;" :
                             status.contains("INTERVIEW") ? "-fx-background-color: rgba(99,102,241,0.2); -fx-text-fill: #818cf8;" :
                             "-fx-background-color: rgba(148,163,184,0.2); -fx-text-fill: #94a3b8;"));
                    setGraphic(badge);
                }
            }
        });

        table.getColumns().addAll(colId, colName, colRole, colScore, colExp, colSkills, colStatus);
        table.setItems(candidateData);

        getChildren().add(table);
    }

    private void runMaxHeapRanking() {
        int topK = topKSpinner.getValue();
        Set<String> skills = selectedFilterSkills.isEmpty() ? Set.of("Java", "SQL", "DSA") : selectedFilterSkills;
        List<Candidate> ranked = context.getCandidateService().rankCandidates(skills, topK);
        candidateData.setAll(ranked);
    }

    private void loadAllCandidates() {
        candidateData.setAll(context.getCandidateService().getAllCandidates());
    }

    private void resetFilters() {
        selectedFilterSkills.clear();
        activeSkillsFilter.getChildren().clear();
        suggestionsPane.getChildren().clear();
        skillSearchField.clear();
        loadAllCandidates();
    }
}
