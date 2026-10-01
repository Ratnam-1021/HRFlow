package com.hrflow.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an employee leave request used for interval overlap conflict analysis.
 */
public class LeaveRequest implements Comparable<LeaveRequest> {
    private final String id;
    private final String employeeId;
    private final String employeeName;
    private final Department department;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private LeaveType type;
    private LeaveStatus status;
    private String reason;

    public enum LeaveType {
        VACATION, SICK_LEAVE, MATERNITY_PATERNITY, PERSONAL, BEREAVEMENT
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, CONFLICT_FLAGGED
    }

    public LeaveRequest(String id, String employeeId, String employeeName,
                        Department department, LocalDate startDate, LocalDate endDate,
                        LeaveType type, String reason) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.id = Objects.requireNonNull(id);
        this.employeeId = Objects.requireNonNull(employeeId);
        this.employeeName = Objects.requireNonNull(employeeName);
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
    }

    public boolean overlapsWith(LeaveRequest other) {
        if (!this.department.equals(other.department)) {
            return false;
        }
        // Two intervals [s1, e1] and [s2, e2] overlap if:
        // s1 <= e2 AND s2 <= e1
        return !this.startDate.isAfter(other.endDate) && !other.startDate.isAfter(this.endDate);
    }

    public String getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public Department getDepartment() { return department; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LeaveType getType() { return type; }
    public void setType(LeaveType type) { this.type = type; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public int compareTo(LeaveRequest o) {
        int cmp = this.startDate.compareTo(o.startDate);
        if (cmp != 0) return cmp;
        return this.endDate.compareTo(o.endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaveRequest that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
