package com.hrflow.service;

import com.hrflow.dsa.search.BinarySearchEngine;
import com.hrflow.dsa.tree.OrgHierarchyTree;
import com.hrflow.model.Department;
import com.hrflow.model.Employee;
import com.hrflow.storage.DatabaseManager;

import java.util.*;

/**
 * Service managing Employees, organizational hierarchy, and search benchmarks.
 */
public class EmployeeService {
    private final DatabaseManager db;
    private final List<Employee> employees;
    private final OrgHierarchyTree orgTree;

    public EmployeeService() {
        this.db = DatabaseManager.getInstance();
        this.employees = new ArrayList<>();
        this.orgTree = new OrgHierarchyTree();
        reload();
    }

    public synchronized void reload() {
        employees.clear();
        employees.addAll(db.loadAllEmployees());
        Collections.sort(employees); // Keep sorted by ID
        orgTree.buildTree(employees);
    }

    public List<Employee> getAllEmployees() {
        return Collections.unmodifiableList(employees);
    }

    public OrgHierarchyTree getOrgTree() {
        return orgTree;
    }

    public Employee findById(String id) {
        int idx = BinarySearchEngine.binarySearchById(employees, id);
        return (idx >= 0) ? employees.get(idx) : null;
    }

    public BinarySearchEngine.BenchmarkResult benchmarkSearch(String targetId) {
        return BinarySearchEngine.compareSearchPerformance(employees, targetId);
    }

    public List<Employee> getEmployeesByDepartment(Department dept) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getDepartment() == dept) {
                result.add(e);
            }
        }
        return result;
    }

    public void addEmployee(Employee emp) {
        employees.add(emp);
        Collections.sort(employees);
        orgTree.addEmployee(emp);
        db.insertEmployees(List.of(emp));
    }
}
