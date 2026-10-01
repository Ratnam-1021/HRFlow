package com.hrflow.dsa.interval;

import com.hrflow.model.Department;
import com.hrflow.model.LeaveRequest;

import java.time.LocalDate;
import java.util.*;

/**
 * Sweep-Line and Interval Overlap algorithm for detecting departmental leave bottlenecks.
 * Automatically flags risks when team concurrency drops below safe operational thresholds.
 */
public class LeaveOverlapDetector {

    public static class ConflictReport {
        private final Department department;
        private final LocalDate peakDate;
        private final int peakConcurrentLeaves;
        private final List<LeaveRequest> conflictingRequests;
        private final String message;

        public ConflictReport(Department department, LocalDate peakDate, int peakConcurrentLeaves,
                              List<LeaveRequest> conflictingRequests, String message) {
            this.department = department;
            this.peakDate = peakDate;
            this.peakConcurrentLeaves = peakConcurrentLeaves;
            this.conflictingRequests = conflictingRequests;
            this.message = message;
        }

        public Department getDepartment() { return department; }
        public LocalDate getPeakDate() { return peakDate; }
        public int getPeakConcurrentLeaves() { return peakConcurrentLeaves; }
        public List<LeaveRequest> getConflictingRequests() { return conflictingRequests; }
        public String getMessage() { return message; }
    }

    private static class Endpoint implements Comparable<Endpoint> {
        final LocalDate date;
        final boolean isStart;
        final LeaveRequest request;

        Endpoint(LocalDate date, boolean isStart, LeaveRequest request) {
            this.date = date;
            this.isStart = isStart;
            this.request = request;
        }

        @Override
        public int compareTo(Endpoint other) {
            int cmp = this.date.compareTo(other.date);
            if (cmp != 0) return cmp;
            // Process START before END on the same date to catch peak concurrency
            return Boolean.compare(!this.isStart, !other.isStart);
        }
    }

    /**
     * Analyzes leave requests using a Sweep-Line algorithm in O(N log N) time.
     *
     * @param requests List of all pending/approved leave requests
     * @param maxAllowedConcurrency Maximum simultaneous absences tolerated in a department (e.g., 2)
     * @return List of conflict reports highlighting bottlenecks
     */
    public List<ConflictReport> analyzeConflicts(Collection<LeaveRequest> requests, int maxAllowedConcurrency) {
        List<ConflictReport> reports = new ArrayList<>();
        if (requests == null || requests.isEmpty()) {
            return reports;
        }

        // Group by department
        Map<Department, List<LeaveRequest>> byDept = new HashMap<>();
        for (LeaveRequest req : requests) {
            byDept.computeIfAbsent(req.getDepartment(), k -> new ArrayList<>()).add(req);
        }

        for (Map.Entry<Department, List<LeaveRequest>> entry : byDept.entrySet()) {
            Department dept = entry.getKey();
            List<LeaveRequest> deptRequests = entry.getValue();

            List<Endpoint> endpoints = new ArrayList<>();
            for (LeaveRequest req : deptRequests) {
                endpoints.add(new Endpoint(req.getStartDate(), true, req));
                endpoints.add(new Endpoint(req.getEndDate(), false, req));
            }

            Collections.sort(endpoints);

            int currentActive = 0;
            int peakConcurrent = 0;
            LocalDate peakDate = null;
            Set<LeaveRequest> activeRequests = new HashSet<>();
            Set<LeaveRequest> conflictedRequests = new HashSet<>();

            for (Endpoint ep : endpoints) {
                if (ep.isStart) {
                    currentActive++;
                    activeRequests.add(ep.request);
                    if (currentActive > peakConcurrent) {
                        peakConcurrent = currentActive;
                        peakDate = ep.date;
                    }
                    if (currentActive > maxAllowedConcurrency) {
                        conflictedRequests.addAll(activeRequests);
                    }
                } else {
                    currentActive--;
                    activeRequests.remove(ep.request);
                }
            }

            if (peakConcurrent > maxAllowedConcurrency && peakDate != null) {
                // Update status of conflicting requests
                for (LeaveRequest r : conflictedRequests) {
                    r.setStatus(LeaveRequest.LeaveStatus.CONFLICT_FLAGGED);
                }

                String msg = String.format("High Risk: %d employees in %s on leave simultaneously on %s (Threshold: %d)",
                        peakConcurrent, dept.getDisplayName(), peakDate, maxAllowedConcurrency);

                reports.add(new ConflictReport(dept, peakDate, peakConcurrent, new ArrayList<>(conflictedRequests), msg));
            }
        }

        return reports;
    }
}
