package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.model.Department;
import com.hrflow.model.HRTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * HR Task Management Hub powered by MaxHeap Priority Queue.
 */
public class TasksView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final ObservableList<HRTask> taskData = FXCollections.observableArrayList();
    private final TableView<HRTask> table = new TableView<>();

    public TasksView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildControlBar();
        buildTable();

        refreshTasks();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Operational Task Dispatch & Priority Queue");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Multi-factor urgency comparator sorts tasks by priority weighting and critical deadlines via MaxHeap.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildControlBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("glass-card");
        bar.setAlignment(Pos.CENTER_LEFT);

        Label info = new Label("Prioritized using MaxHeap: Critical items float to top automatically.");
        info.setStyle("-fx-font-size: 13px; -fx-text-fill: #cbd5e1;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button completeBtn = new Button("✓ Mark Selected Done");
        completeBtn.getStyleClass().add("btn-emerald");
        completeBtn.setOnAction(e -> {
            HRTask selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                context.getTaskService().updateStatus(selected, HRTask.TaskStatus.COMPLETED);
                refreshTasks();
            }
        });

        Button addQuickTaskBtn = new Button("+ Add Urgent HR Task");
        addQuickTaskBtn.getStyleClass().add("btn-primary");
        addQuickTaskBtn.setOnAction(e -> openAddTaskDialog());

        bar.getChildren().addAll(info, spacer, completeBtn, addQuickTaskBtn);
        getChildren().add(bar);
    }

    private void buildTable() {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<HRTask, HRTask.Priority> colPriority = new TableColumn<>("Priority (Heap Key)");
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colPriority.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(HRTask.Priority p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(p.name());
                    badge.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-weight: bold; " +
                            (p == HRTask.Priority.CRITICAL ? "-fx-background-color: rgba(244,63,94,0.25); -fx-text-fill: #fb7185;" :
                             p == HRTask.Priority.HIGH ? "-fx-background-color: rgba(245,158,11,0.25); -fx-text-fill: #fbbf24;" :
                             p == HRTask.Priority.MEDIUM ? "-fx-background-color: rgba(99,102,241,0.2); -fx-text-fill: #818cf8;" :
                             "-fx-background-color: rgba(148,163,184,0.2); -fx-text-fill: #94a3b8;"));
                    setGraphic(badge);
                }
            }
        });
        colPriority.setMaxWidth(140);

        TableColumn<HRTask, String> colTitle = new TableColumn<>("Task Description");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<HRTask, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDepartment() != null ? cellData.getValue().getDepartment().getDisplayName() : "General HR"));

        TableColumn<HRTask, LocalDate> colDeadline = new TableColumn<>("Target Deadline");
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        TableColumn<HRTask, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().name()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) setText(null);
                else {
                    setText(status);
                    setStyle(status.equals("COMPLETED") ? "-fx-text-fill: #34d399; -fx-font-weight: bold;" : "-fx-text-fill: #cbd5e1;");
                }
            }
        });
        colStatus.setMaxWidth(110);

        table.getColumns().addAll(colPriority, colTitle, colDept, colDeadline, colStatus);
        table.setItems(taskData);

        card.getChildren().add(table);
        getChildren().add(card);
    }

    private void openAddTaskDialog() {
        Dialog<HRTask> dialog = new Dialog<>();
        dialog.setTitle("Create Priority HR Action");

        TextField titleField = new TextField();
        titleField.setPromptText("Task Title");
        ComboBox<HRTask.Priority> priorityCombo = new ComboBox<>(FXCollections.observableArrayList(HRTask.Priority.values()));
        priorityCombo.setValue(HRTask.Priority.HIGH);

        ComboBox<Department> deptCombo = new ComboBox<>(FXCollections.observableArrayList(Department.values()));
        deptCombo.setValue(Department.HUMAN_RESOURCES);

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(2));

        VBox content = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Priority Level:"), priorityCombo,
                new Label("Department:"), deptCombo,
                new Label("Deadline:"), datePicker
        );
        content.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(content);

        ButtonType addBtnType = new ButtonType("Add to Priority Heap", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == addBtnType && !titleField.getText().trim().isEmpty()) {
                return new HRTask(
                        "TSK-" + System.currentTimeMillis() % 10000,
                        titleField.getText().trim(),
                        "",
                        priorityCombo.getValue(),
                        datePicker.getValue(),
                        deptCombo.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            context.getTaskService().addTask(task);
            refreshTasks();
        });
    }

    private void refreshTasks() {
        List<HRTask> prioritized = context.getTaskService().getPrioritizedPendingTasks(50);
        taskData.setAll(prioritized);
    }
}
