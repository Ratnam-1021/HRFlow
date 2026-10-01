package com.hrflow.service;

import com.hrflow.dsa.graph.InterviewScheduler;
import com.hrflow.dsa.interval.LeaveOverlapDetector;
import com.hrflow.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * HR Automation Center orchestrator.
 * Executes the complete algorithmic daily pipeline across all modules in background threads.
 */
public class AutomationCenterService {
    private final CandidateService candidateService;
    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final SchedulingService schedulingService;
    private final TaskService taskService;

    public static class AutomationResult {
        public int totalCandidatesProcessed;
        public int shortlistedCount;
        public List<Candidate> topCandidates;
        public int scheduledInterviewsCount;
        public List<InterviewScheduler.InterviewBooking> schedule;
        public int leaveConflictsDetected;
        public List<LeaveOverlapDetector.ConflictReport> leaveConflicts;
        public int urgentTasksCreated;
        public long executionTimeMillis;
    }

    public AutomationCenterService(CandidateService cs, EmployeeService es,
                                   LeaveService ls, SchedulingService ss, TaskService ts) {
        this.candidateService = cs;
        this.employeeService = es;
        this.leaveService = ls;
        this.schedulingService = ss;
        this.taskService = ts;
    }

    /**
     * Runs the end-to-end daily automation pipeline asynchronously.
     * Emits progress events to the consumer for live UI updates.
     */
    public CompletableFuture<AutomationResult> runDailyAutomationAsync(Consumer<String> progressListener) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            AutomationResult result = new AutomationResult();

            // Step 1: Candidate Screening & Ranking via MaxHeap
            progressListener.accept("Scanning applicant pool and evaluating skills with Trie...");
            sleep(400);

            List<Candidate> allApplicants = candidateService.getAllCandidates();
            result.totalCandidatesProcessed = allApplicants.size();

            Set<String> targetSkills = Set.of("Java", "Spring Boot", "SQL", "DSA");
            List<Candidate> topRanked = candidateService.rankCandidates(targetSkills, 15);
            result.shortlistedCount = topRanked.size();
            result.topCandidates = topRanked;

            // Shortlist top candidates
            for (Candidate c : topRanked) {
                candidateService.updateCandidateStatus(c, Candidate.CandidateStatus.SHORTLISTED);
            }
            progressListener.accept(String.format("Screened %d candidates. Prioritized top %d via MaxHeap.",
                    result.totalCandidatesProcessed, result.shortlistedCount));
            sleep(400);

            // Step 2: Conflict-free Interview Scheduling via Graph Coloring
            progressListener.accept("Running Graph Conflict Coloring for interview allocations...");
            sleep(400);

            List<Employee> interviewers = employeeService.getEmployeesByDepartment(Department.ENGINEERING);
            if (interviewers.isEmpty()) {
                interviewers = employeeService.getAllEmployees();
            }

            // Schedule the top 8 candidates
            List<Candidate> toInterview = topRanked.subList(0, Math.min(8, topRanked.size()));
            List<InterviewScheduler.InterviewBooking> bookings = schedulingService.autoSchedule(
                    toInterview, interviewers, LocalDate.now().plusDays(1)
            );
            result.scheduledInterviewsCount = bookings.size();
            result.schedule = bookings;
            progressListener.accept(String.format("Scheduled %d zero-conflict interviews across rooms and panels.",
                    result.scheduledInterviewsCount));
            sleep(400);

            // Step 3: Sweep-line Department Leave Bottleneck Detection
            progressListener.accept("Analyzing departmental absence overlaps using Sweep-Line intervals...");
            sleep(400);

            List<LeaveOverlapDetector.ConflictReport> conflicts = leaveService.detectConflicts(2);
            result.leaveConflictsDetected = conflicts.size();
            result.leaveConflicts = conflicts;

            progressListener.accept(String.format("Detected %d operational leave bottleneck alerts.",
                    result.leaveConflictsDetected));
            sleep(400);

            // Step 4: Prioritized Daily Task Dispatch via TaskPriorityQueue
            progressListener.accept("Formulating prioritized HR operational action items...");
            sleep(300);

            int newTaskCount = 0;
            if (!conflicts.isEmpty()) {
                taskService.addTask(new HRTask(
                        "AUTO-TSK-" + System.currentTimeMillis(),
                        "Mitigate " + conflicts.get(0).getDepartment().getDisplayName() + " Staffing Bottleneck",
                        conflicts.get(0).getMessage(),
                        HRTask.Priority.CRITICAL,
                        LocalDate.now().plusDays(1),
                        conflicts.get(0).getDepartment()
                ));
                newTaskCount++;
            }

            if (!toInterview.isEmpty()) {
                taskService.addTask(new HRTask(
                        "AUTO-TSK-" + (System.currentTimeMillis() + 1),
                        "Send Interview Invitations to " + toInterview.size() + " Shortlisted Candidates",
                        "Automated graph schedule ready for tomorrow's panel sessions.",
                        HRTask.Priority.HIGH,
                        LocalDate.now(),
                        Department.HUMAN_RESOURCES
                ));
                newTaskCount++;
            }

            result.urgentTasksCreated = newTaskCount;
            result.executionTimeMillis = System.currentTimeMillis() - startTime;

            progressListener.accept(String.format("Daily Automation Complete! Processed in %d ms.", result.executionTimeMillis));
            return result;
        });
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}
