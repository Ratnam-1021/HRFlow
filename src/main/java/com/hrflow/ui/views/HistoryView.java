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
 * Visualizes the LIFO Undo Stack, Redo Stack, and full audit log.
 */
public class HistoryView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<HRAction> historyData = FXCollections.observableArrayList();
    private final TableView<HRAction> table = new TableView<>();
    private final Label undoCountLabel = new Label("Undo Stack Depth: 0");
    private final Label redoCountLabel = new Label("Redo Stack Depth: 0");
    private final Button undoBtn = new Button("↩ Undo (Pop Stack)");
    private final Button redoBtn = new Button("↪ Redo (Pop Redo)");

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
        Label title = new Label("Recruitment Action History & Stack Control");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Dual-Stack LIFO architecture tracking candidate status transitions with reversible Undo & Redo operations.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("glass-card");
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox statsBox = new VBox(4);
        undoCountLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: #34d399;");
        redoCountLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: #38bdf8;");
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
        colTime.setStyle("-fx-text-fill: #94a3b8;");

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
                    badge.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-weight: bold; " +
                            (type == HRAction.ActionType.SHORTLIST ? "-fx-background-color: rgba(16,185,129,0.2); -fx-text-fill: #34d399;" :
                             type == HRAction.ActionType.REJECT ? "-fx-background-color: rgba(244,63,94,0.2); -fx-text-fill: #fb7185;" :
                             "-fx-background-color: rgba(99,102,241,0.2); -fx-text-fill: #818cf8;"));
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
        undoCountLabel.setText("Undo Stack (LIFO depth): " + stack.undoSize() + " items");
        redoCountLabel.setText("Redo Stack (LIFO depth): " + stack.redoSize() + " items");
        undoBtn.setDisable(!stack.canUndo());
        redoBtn.setDisable(!stack.canRedo());
    }
}
