package com.hrflow.ui.views;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.search.BinarySearchEngine;
import com.hrflow.dsa.tree.OrgHierarchyTree;
import com.hrflow.model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Random;

/**
 * Organization Hierarchy & Search Benchmark View.
 * Showcases N-ary Org Hierarchy Tree and live microsecond benchmark comparing Binary Search against Linear Search.
 */
public class EmployeesView extends VBox {
    private final AppContext context = AppContext.getInstance();
    private final TreeView<String> treeView = new TreeView<>();
    private final TextField searchField = new TextField();
    private final Label benchmarkResultsLabel = new Label("Execute a search to view comparison metrics.");
    private final VBox comparisonStatsCard = new VBox(10);
    private final VBox employeeDetailCard = new VBox(8);

    public EmployeesView() {
        setSpacing(18);
        setPadding(new Insets(24));

        buildHeader();
        buildSplitContent();

        populateOrgTree();
    }

    private void buildHeader() {
        VBox titleBox = new VBox(4);
        Label title = new Label("Workforce Hierarchy & Algorithmic Search");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label subtitle = new Label("Traverse reporting chains via N-ary Tree and test O(log N) Binary Search latency against O(N) linear scans.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        titleBox.getChildren().addAll(title, subtitle);
        getChildren().add(titleBox);
    }

    private void buildSplitContent() {
        HBox split = new HBox(20);
        VBox.setVgrow(split, Priority.ALWAYS);

        // Left Panel: Org Tree
        VBox leftCard = new VBox(12);
        leftCard.getStyleClass().add("glass-card");
        HBox.setHgrow(leftCard, Priority.ALWAYS);

        Label treeTitle = new Label("Organizational Tree Hierarchy (DFS / BFS)");
        treeTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        treeView.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(treeView, Priority.ALWAYS);
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue().contains("[")) {
                String val = newVal.getValue();
                int start = val.indexOf('[') + 1;
                int end = val.indexOf(']');
                if (start > 0 && end > start) {
                    String empId = val.substring(start, end);
                    searchField.setText(empId);
                    runSearchBenchmark(empId);
                }
            }
        });

        leftCard.getChildren().addAll(treeTitle, treeView);

        // Right Panel: Binary Search vs Linear Search Benchmark Hub
        VBox rightCard = new VBox(14);
        rightCard.getStyleClass().add("glass-card");
        rightCard.setPrefWidth(460);

        Label searchTitle = new Label("Binary Search vs Linear Search Benchmark");
        searchTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchField.setPromptText("Enter Employee ID (e.g. EMP-0009)");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button benchmarkBtn = new Button("⚡ Benchmark");
        benchmarkBtn.getStyleClass().add("btn-primary");
        benchmarkBtn.setOnAction(e -> runSearchBenchmark(searchField.getText().trim()));

        Button randomBtn = new Button("Random");
        randomBtn.getStyleClass().add("btn-secondary");
        randomBtn.setOnAction(e -> pickRandomEmployee());

        searchRow.getChildren().addAll(searchField, benchmarkBtn, randomBtn);

        // Stats card
        comparisonStatsCard.setStyle("-fx-background-color: #1a253c; -fx-padding: 14; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");
        benchmarkResultsLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        comparisonStatsCard.getChildren().add(benchmarkResultsLabel);

        // Employee details card
        employeeDetailCard.setStyle("-fx-background-color: #1a253c; -fx-padding: 14; -fx-background-radius: 8; -fx-border-color: #1e293b; -fx-border-radius: 8;");
        Label detailTitle = new Label("Selected Employee Dossier");
        detailTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cbd5e1;");
        Label detailPlaceholder = new Label("Select a node from tree or benchmark an employee.");
        detailPlaceholder.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        employeeDetailCard.getChildren().addAll(detailTitle, detailPlaceholder);

        rightCard.getChildren().addAll(searchTitle, searchRow, comparisonStatsCard, employeeDetailCard);

        split.getChildren().addAll(leftCard, rightCard);
        getChildren().add(split);
    }

    private void populateOrgTree() {
        OrgHierarchyTree tree = context.getEmployeeService().getOrgTree();
        OrgHierarchyTree.Node rootNode = tree.getRoot();
        if (rootNode == null) return;

        TreeItem<String> rootItem = buildTreeItem(rootNode);
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
    }

    private TreeItem<String> buildTreeItem(OrgHierarchyTree.Node node) {
        Employee emp = node.getEmployee();
        int directReports = node.getDirectReports().size();
        int totalSubtree = context.getEmployeeService().getOrgTree().countSubtreeHeadcount(emp.getId());

        String label = String.format("[%s] %s — %s (%s) %s",
                emp.getId(), emp.getName(), emp.getRole(), emp.getDepartment().getDisplayName(),
                totalSubtree > 0 ? "👥 " + totalSubtree + " reports" : "");

        TreeItem<String> item = new TreeItem<>(label);
        item.setExpanded(true);

        for (OrgHierarchyTree.Node child : node.getDirectReports()) {
            item.getChildren().add(buildTreeItem(child));
        }

        return item;
    }

    private void runSearchBenchmark(String targetId) {
        if (targetId == null || targetId.isEmpty()) return;

        List<Employee> all = context.getEmployeeService().getAllEmployees();
        BinarySearchEngine.BenchmarkResult res = context.getEmployeeService().benchmarkSearch(targetId);

        comparisonStatsCard.getChildren().clear();

        Label header = new Label("⚡ Algorithm Performance Benchmark (N = " + res.getTotalRecords() + ")");
        header.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        HBox linearRow = new HBox(8);
        Label lTitle = new Label("Linear Search:  ");
        lTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f43f5e;");
        Label lVal = new Label(String.format("%d ns | %d comparisons (O(N))", res.getLinearSearchNanos(), res.getLinearComparisons()));
        lVal.setStyle("-fx-text-fill: #cbd5e1;");
        linearRow.getChildren().addAll(lTitle, lVal);

        HBox binaryRow = new HBox(8);
        Label bTitle = new Label("Binary Search: ");
        bTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #34d399;");
        Label bVal = new Label(String.format("%d ns | %d comparisons (O(log N))", res.getBinarySearchNanos(), res.getBinaryComparisons()));
        bVal.setStyle("-fx-text-fill: #cbd5e1;");
        binaryRow.getChildren().addAll(bTitle, bVal);

        Label speedup = new Label(String.format("Efficiency Gain: Binary Search required only %d steps vs %d steps (Logarithmic Advantage)",
                res.getBinaryComparisons(), res.getLinearComparisons()));
        speedup.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: 600; -fx-font-size: 11.5px;");

        comparisonStatsCard.getChildren().addAll(header, linearRow, binaryRow, speedup);

        // Update Dossier
        if (res.getFoundEmployee() != null) {
            updateEmployeeDossier(res.getFoundEmployee());
        }
    }

    private void updateEmployeeDossier(Employee emp) {
        employeeDetailCard.getChildren().clear();

        Label detailTitle = new Label("Selected Employee Dossier: " + emp.getName());
        detailTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        VBox grid = new VBox(6);
        grid.getChildren().addAll(
                createDetailRow("Employee ID:", emp.getId()),
                createDetailRow("Official Role:", emp.getRole()),
                createDetailRow("Department:", emp.getDepartment().getDisplayName()),
                createDetailRow("Manager ID:", emp.getManagerId() != null ? emp.getManagerId() : "Executive Root (None)"),
                createDetailRow("Attendance Rate:", String.format("%.1f %%", emp.getAttendanceRate())),
                createDetailRow("Performance Score:", String.format("%.1f / 100", emp.getPerformanceScore()))
        );

        // Reporting Chain from Org Tree
        List<Employee> chain = context.getEmployeeService().getOrgTree().getReportingChain(emp.getId());
        StringBuilder chainStr = new StringBuilder();
        for (int i = 0; i < chain.size(); i++) {
            chainStr.append(chain.get(i).getName());
            if (i < chain.size() - 1) chainStr.append("  ➜  ");
        }

        Label chainLabel = new Label("Reporting Lineage (DFS): " + chainStr);
        chainLabel.setWrapText(true);
        chainLabel.setStyle("-fx-text-fill: #818cf8; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6 0 0 0;");

        employeeDetailCard.getChildren().addAll(detailTitle, grid, chainLabel);
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(8);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-min-width: 120px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: 600;");
        row.getChildren().addAll(l, v);
        return row;
    }

    private void pickRandomEmployee() {
        List<Employee> emps = context.getEmployeeService().getAllEmployees();
        if (!emps.isEmpty()) {
            Employee randomEmp = emps.get(new Random().nextInt(emps.size()));
            searchField.setText(randomEmp.getId());
            runSearchBenchmark(randomEmp.getId());
        }
    }
}
