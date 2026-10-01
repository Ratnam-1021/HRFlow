package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.model.Candidate;
import com.hrflow.model.HRAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.*;

/**
 * Recruitment & Hiring View.
 * Soft Warm Light Yellowish-Orange Theme.
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
    private final Button undoBtn = new Button("↩ Undo");
    private final Button redoBtn = new Button("↪ Redo");
    private final Label historyStatusLabel = new Label("Ready — No recent status changes.");

    public RecruitmentView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildControlBar();
        buildTable();

        loadAllCandidates();
        updateUndoRedoState();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Candidate Pipeline & Screening");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #261B12;");
        Label subtitle = new Label("Search applicants by skills, rank candidates by overall fit score, and manage shortlisting status.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #786958;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        VBox controlCard = new VBox(12);
        controlCard.getStyleClass().add("glass-card");

        HBox row1 = new HBox(14);
        row1.setAlignment(Pos.CENTER_LEFT);

        VBox searchBox = new VBox(4);
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        Label searchLabel = new Label("Search by Skill:");
        searchLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #5C4D3E;");

        skillSearchField.setPromptText("Type a skill (e.g. Java, Python, React, SQL, AWS)...");
        skillSearchField.getStyleClass().add("search-field");
        skillSearchField.textProperty().addListener((obs, oldVal, newVal) -> updateLinearSuggestions(newVal));

        searchBox.getChildren().addAll(searchLabel, skillSearchField);

        VBox spinnerBox = new VBox(4);
        Label spinnerLabel = new Label("Top Candidates:");
        spinnerLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #5C4D3E;");
        topKSpinner.setPrefWidth(90);
        spinnerBox.getChildren().addAll(spinnerLabel, topKSpinner);

        Button rankBtn = new Button("⚡ Rank Candidates");
        rankBtn.getStyleClass().add("btn-primary");
        rankBtn.setStyle("-fx-padding: 9 16;");
        rankBtn.setOnAction(e -> runSelectionSortRanking());

        Button insertionBtn = new Button("Sort by Score");
        insertionBtn.getStyleClass().add("btn-secondary");
        insertionBtn.setOnAction(e -> runInsertionSort());

        // Undo & Redo buttons
        undoBtn.getStyleClass().add("btn-secondary");
        undoBtn.setOnAction(e -> handleUndo());

        redoBtn.getStyleClass().add("btn-secondary");
        redoBtn.setOnAction(e -> handleRedo());

        Button resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("btn-secondary");
        resetBtn.setOnAction(e -> resetFilters());

        row1.getChildren().addAll(searchBox, spinnerBox, rankBtn, insertionBtn, undoBtn, redoBtn, resetBtn);

        // Autocomplete suggestions chip bar
        suggestionsPane.setHgap(8);
        suggestionsPane.setVgap(8);

        // Active filtered skills chip bar
        activeSkillsFilter.setHgap(8);
        activeSkillsFilter.setVgap(8);

        // History status
        historyStatusLabel.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #C2410C; -fx-font-weight: 600; -fx-font-style: italic;");

        controlCard.getChildren().addAll(row1, suggestionsPane, activeSkillsFilter, historyStatusLabel);
        getChildren().add(controlCard);
    }

    private void updateLinearSuggestions(String query) {
        suggestionsPane.getChildren().clear();
        if (query == null || query.trim().length() < 1) {
            return;
        }

        List<String> suggestions = context.getCandidateService().autocompleteSkills(query, 6);
        if (suggestions.isEmpty()) {
            Label noMatch = new Label("No matching skill found");
            noMatch.setStyle("-fx-text-fill: #A89A88; -fx-font-size: 11.5px; -fx-font-style: italic;");
            suggestionsPane.getChildren().add(noMatch);
            return;
        }

        for (String skill : suggestions) {
            Button chip = new Button("+ " + skill);
            chip.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-cursor: hand; -fx-border-color: #FDE68A; -fx-border-radius: 12;");
            chip.setOnAction(e -> addFilterSkill(skill));
            suggestionsPane.getChildren().add(chip);
        }
    }

    private void addFilterSkill(String skill) {
        if (selectedFilterSkills.add(skill)) {
            renderFilterChips();
            runSelectionSortRanking();
        }
    }

    private void removeFilterSkill(String skill) {
        selectedFilterSkills.remove(skill);
        renderFilterChips();
        runSelectionSortRanking();
    }

    private void renderFilterChips() {
        activeSkillsFilter.getChildren().clear();
        for (String skill : selectedFilterSkills) {
            HBox chip = new HBox(6);
            chip.setAlignment(Pos.CENTER);
            chip.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 12; -fx-padding: 3 9;");
            Label label = new Label(skill);
            label.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 11.5px; -fx-font-weight: 700;");
            Button remove = new Button("×");
            remove.setStyle("-fx-background-color: transparent; -fx-text-fill: #FFEDD5; -fx-padding: 0; -fx-font-weight: bold; -fx-cursor: hand;");
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

        TableColumn<Candidate, Double> colScore = new TableColumn<>("Ranking Score");
        colScore.setCellValueFactory(new PropertyValueFactory<>("overallScore"));
        colScore.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f %%", score));
                    setStyle("-fx-font-weight: 700; -fx-text-fill: " +
                            (score >= 85 ? "#15803D" : score >= 70 ? "#C2410C" : "#B45309"));
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
                    if (status.contains("SHORTLIST")) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else if (status.contains("INTERVIEW")) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else if (status.contains("REJECT")) {
                        badge.setStyle("-fx-background-color: #FFE4E6; -fx-text-fill: #BE123C; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else {
                        badge.setStyle("-fx-background-color: #F4EAE0; -fx-text-fill: #6E5F50; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Quick Action column
        TableColumn<Candidate, Void> colAction = new TableColumn<>("Actions");
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnShortlist = new Button("Shortlist");
            private final Button btnReject = new Button("Reject");
            private final HBox pane = new HBox(6, btnShortlist, btnReject);

            {
                btnShortlist.setStyle("-fx-background-color: #15803D; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: 600; -fx-padding: 3 8; -fx-background-radius: 6; -fx-cursor: hand;");
                btnReject.setStyle("-fx-background-color: #BE123C; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: 600; -fx-padding: 3 8; -fx-background-radius: 6; -fx-cursor: hand;");

                btnShortlist.setOnAction(e -> {
                    Candidate c = getTableView().getItems().get(getIndex());
                    context.getCandidateService().updateCandidateStatus(c, Candidate.CandidateStatus.SHORTLISTED);
                    table.refresh();
                    updateUndoRedoState();
                });

                btnReject.setOnAction(e -> {
                    Candidate c = getTableView().getItems().get(getIndex());
                    context.getCandidateService().updateCandidateStatus(c, Candidate.CandidateStatus.REJECTED);
                    table.refresh();
                    updateUndoRedoState();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(colId, colName, colRole, colScore, colExp, colSkills, colStatus, colAction);
        table.setItems(candidateData);

        getChildren().add(table);
    }

    private void runSelectionSortRanking() {
        int topK = topKSpinner.getValue();
        Set<String> skills = selectedFilterSkills.isEmpty() ? Set.of("Java", "SQL") : selectedFilterSkills;
        List<Candidate> ranked = context.getCandidateService().rankCandidates(skills, topK);
        candidateData.setAll(ranked);
    }

    private void runInsertionSort() {
        List<Candidate> sorted = context.getCandidateService().getCandidatesSortedByInsertionSort();
        candidateData.setAll(sorted);
    }

    private void handleUndo() {
        HRAction undone = context.getCandidateService().undoLastAction();
        if (undone != null) {
            table.refresh();
            historyStatusLabel.setText("↩ Undone: " + undone.getDescription());
        }
        updateUndoRedoState();
    }

    private void handleRedo() {
        HRAction redone = context.getCandidateService().redoLastAction();
        if (redone != null) {
            table.refresh();
            historyStatusLabel.setText("↪ Redone: " + redone.getDescription());
        }
        updateUndoRedoState();
    }

    private void updateUndoRedoState() {
        undoBtn.setDisable(!context.getCandidateService().canUndo());
        redoBtn.setDisable(!context.getCandidateService().canRedo());

        HRAction peek = context.getCandidateService().getActionStack().peekUndo();
        if (peek != null) {
            historyStatusLabel.setText("Last action: " + peek.getDescription());
        }
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
