package com.hrflow.service;

import com.hrflow.dsa.interval.LeaveOverlapDetector;
import com.hrflow.model.LeaveRequest;
import com.hrflow.storage.DatabaseManager;

import java.util.*;

/**
 * Service managing leave requests and algorithmic overlap bottleneck detection.
 */
public class LeaveService {
    private final DatabaseManager db;
    private final List<LeaveRequest> leaveRequests;
    private final LeaveOverlapDetector overlapDetector;

    public LeaveService() {
        this.db = DatabaseManager.getInstance();
        this.leaveRequests = new ArrayList<>();
        this.overlapDetector = new LeaveOverlapDetector();
        reload();
    }

    public synchronized void reload() {
        leaveRequests.clear();
        leaveRequests.addAll(db.loadAllLeaveRequests());
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return Collections.unmodifiableList(leaveRequests);
    }

    /**
     * Executes the Sweep-Line interval analysis.
     */
    public List<LeaveOverlapDetector.ConflictReport> detectConflicts(int maxAllowedConcurrency) {
        return overlapDetector.analyzeConflicts(leaveRequests, maxAllowedConcurrency);
    }

    public void addLeaveRequest(LeaveRequest request) {
        leaveRequests.add(request);
        db.insertLeaveRequests(List.of(request));
    }

    public void updateStatus(LeaveRequest request, LeaveRequest.LeaveStatus newStatus) {
        request.setStatus(newStatus);
        db.insertLeaveRequests(List.of(request));
    }
}
