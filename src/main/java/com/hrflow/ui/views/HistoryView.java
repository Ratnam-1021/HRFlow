package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.stack.HRActionStack;
import com.hrflow.model.HRAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Subsystem Pillar 3: Action History & Dual-Stack Undo / Redo Hub.
 * Soft Warm Light Yellowish-Orange Theme.
 */
public class HistoryView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<HRAction> historyData = FXCollections.observableArrayList();
    private final TableView<HRAction> table = new TableView<>();
    private final Label undoCountLabel = new Label("Undoable Actions: 0");
    private final Label redoCountLabel = new Label("Redoable Actions: 0");
    private final Button undoBtn = new Button("↩ Undo Action");
    private final Button redoBtn = new Button("↪ Redo Action");

    public HistoryView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildControlBar();
        buildTable();

        refreshHistory();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Recruitment Activity & Audit Log");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #261B12;");
        Label subtitle = new Label("Real-time chronological log of candidate status updates, shortlists, and rejections with one-click undo.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #786958;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("glass-card");
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox statsBox = new VBox(4);
        undoCountLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 700; -fx-text-fill: #15803D;");
        redoCountLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 700; -fx-text-fill: #C2410C;");
        statsBox.getChildren().addAll(undoCountLabel, redoCountLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        undoBtn.getStyleClass().add("btn-primary");
        undoBtn.setOnAction(e -> handleUndo());

        redoBtn.getStyleClass().add("btn-secondary");
        redoBtn.setOnAction(e -> handleRedo());

        Button refreshBtn = new Button("Refresh Log");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> refreshHistory());

        bar.getChildren().addAll(statsBox, spacer, undoBtn, redoBtn, refreshBtn);
        getChildren().add(bar);
    }

    private void buildTable() {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<HRAction, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));
        colTime.setMaxWidth(110);
        colTime.setStyle("-fx-text-fill: #786958; -fx-font-weight: 600;");

        TableColumn<HRAction, HRAction.ActionType> colType = new TableColumn<>("Action Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(HRAction.ActionType type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(type.name());
                    if (type == HRAction.ActionType.SHORTLIST) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else if (type == HRAction.ActionType.REJECT) {
                        badge.setStyle("-fx-background-color: #FFE4E6; -fx-text-fill: #BE123C; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    } else {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8;");
                    }
                    setGraphic(badge);
                }
            }
        });
        colType.setMaxWidth(160);

        TableColumn<HRAction, String> colDesc = new TableColumn<>("Action Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(colTime, colType, colDesc);
        table.setItems(historyData);

        card.getChildren().add(table);
        getChildren().add(card);
    }

    private void handleUndo() {
        context.getCandidateService().undoLastAction();
        refreshHistory();
    }

    private void handleRedo() {
        context.getCandidateService().redoLastAction();
        refreshHistory();
    }

    public void refreshHistory() {
        HRActionStack stack = context.getCandidateService().getActionStack();
        historyData.setAll(stack.getHistoryAuditLog());
        undoCountLabel.setText("Undoable Actions: " + stack.undoSize());
        redoCountLabel.setText("Redoable Actions: " + stack.redoSize());
        undoBtn.setDisable(!stack.canUndo());
        redoBtn.setDisable(!stack.canRedo());
    }
}
