package com.hrflow.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an employee in the organizational hierarchy.
 */
public class Employee implements Comparable<Employee> {
    private final String id;
    private String name;
    private String email;
    private Department department;
    private String role;
    private String managerId; // null for CEO / Root
    private double salary;
    private LocalDate joiningDate;
    private double attendanceRate; // e.g. 96.5%
    private double performanceScore; // 0 - 100

    public Employee(String id, String name, String email, Department department,
                    String role, String managerId, double salary,
                    LocalDate joiningDate, double attendanceRate, double performanceScore) {
        this.id = Objects.requireNonNull(id, "Employee ID is required");
        this.name = Objects.requireNonNull(name, "Name is required");
        this.email = email;
        this.department = department;
        this.role = role;
        this.managerId = managerId;
        this.salary = salary;
        this.joiningDate = (joiningDate != null) ? joiningDate : LocalDate.now();
        this.attendanceRate = attendanceRate;
        this.performanceScore = performanceScore;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(double attendanceRate) { this.attendanceRate = attendanceRate; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }

    @Override
    public int compareTo(Employee o) {
        // Natural sort by Employee ID
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee employee)) return false;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", id, name, role, department.getDisplayName());
    }
}
