package com.hrflow.dsa.tree;

import com.hrflow.model.Department;
import com.hrflow.model.Employee;

import java.util.*;

/**
 * N-ary Organization Hierarchy Tree.
 * Supports tree traversals (BFS, DFS), subtree headcount calculations,
 * and reporting path tracing from any employee up to the CEO.
 */
public class OrgHierarchyTree {

    public static class Node {
        private final Employee employee;
        private Node parent;
        private final List<Node> directReports;

        public Node(Employee employee) {
            this.employee = Objects.requireNonNull(employee);
            this.directReports = new ArrayList<>();
        }

        public Employee getEmployee() { return employee; }
        public Node getParent() { return parent; }
        public void setParent(Node parent) { this.parent = parent; }
        public List<Node> getDirectReports() { return directReports; }
        public void addReport(Node report) {
            this.directReports.add(report);
            report.setParent(this);
        }
    }

    private Node root;
    private final Map<String, Node> nodeMap; // O(1) employee ID to Node lookup

    public OrgHierarchyTree() {
        this.nodeMap = new HashMap<>();
    }

    public void addEmployee(Employee employee) {
        if (employee == null) return;

        Node newNode = nodeMap.computeIfAbsent(employee.getId(), id -> new Node(employee));

        if (employee.getManagerId() == null) {
            // Root (e.g. CEO)
            this.root = newNode;
        } else {
            Node managerNode = nodeMap.get(employee.getManagerId());
            if (managerNode != null) {
                managerNode.addReport(newNode);
            }
        }
    }

    public void buildTree(Collection<Employee> employees) {
        nodeMap.clear();
        root = null;

        // Pass 1: create all nodes
        for (Employee e : employees) {
            nodeMap.put(e.getId(), new Node(e));
        }

        // Pass 2: stitch parent/child references
        for (Employee e : employees) {
            Node current = nodeMap.get(e.getId());
            if (e.getManagerId() == null || e.getManagerId().trim().isEmpty()) {
                this.root = current;
            } else {
                Node manager = nodeMap.get(e.getManagerId());
                if (manager != null) {
                    manager.addReport(current);
                }
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    public Employee findEmployee(String id) {
        Node n = nodeMap.get(id);
        return (n != null) ? n.getEmployee() : null;
    }

    /**
     * Computes the total headcount reporting under this manager (including indirect reports)
     * using Depth-First Search (DFS).
     */
    public int countSubtreeHeadcount(String managerId) {
        Node node = nodeMap.get(managerId);
        if (node == null) return 0;
        return dfsCount(node) - 1; // exclude the manager themselves
    }

    private int dfsCount(Node current) {
        int count = 1;
        for (Node report : current.getDirectReports()) {
            count += dfsCount(report);
        }
        return count;
    }

    /**
     * Breadth-First Search (BFS) level-order traversal for organizational tiers.
     */
    public List<List<Employee>> getHierarchyByLevels() {
        List<List<Employee>> levels = new ArrayList<>();
        if (root == null) return levels;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Employee> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                Node curr = queue.poll();
                currentLevel.add(curr.getEmployee());
                for (Node child : curr.getDirectReports()) {
                    queue.offer(child);
                }
            }
            levels.add(currentLevel);
        }

        return levels;
    }

    /**
     * Traces the reporting path from an employee upward to the executive root.
     */
    public List<Employee> getReportingChain(String employeeId) {
        List<Employee> chain = new ArrayList<>();
        Node current = nodeMap.get(employeeId);
        while (current != null) {
            chain.add(current.getEmployee());
            current = current.getParent();
        }
        return chain;
    }

    /**
     * Computes department headcount across the tree.
     */
    public Map<Department, Integer> getDepartmentDistribution() {
        Map<Department, Integer> dist = new HashMap<>();
        for (Node n : nodeMap.values()) {
            Department d = n.getEmployee().getDepartment();
            dist.put(d, dist.getOrDefault(d, 0) + 1);
        }
        return dist;
    }

    public int getTotalEmployees() {
        return nodeMap.size();
    }
}
