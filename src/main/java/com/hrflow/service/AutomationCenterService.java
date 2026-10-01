package com.hrflow.service;

import com.hrflow.dsa.interval.LeaveOverlapDetector;
import com.hrflow.dsa.queue.QueueInterviewScheduler;
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
        public List<QueueInterviewScheduler.InterviewBooking> schedule;
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

            // Step 1: Candidate Screening & Ranking via Selection Sort
            progressListener.accept("Scanning applicant pool with HashMap & Linear filtering...");
            sleep(350);

            List<Candidate> allApplicants = candidateService.getAllCandidates();
            result.totalCandidatesProcessed = allApplicants.size();

            Set<String> targetSkills = Set.of("Java", "Spring Boot", "SQL");
            List<Candidate> topRanked = candidateService.rankCandidates(targetSkills, 15);
            result.shortlistedCount = topRanked.size();
            result.topCandidates = topRanked;

            // Shortlist top candidates (tracked on Undo Stack)
            for (Candidate c : topRanked) {
                candidateService.updateCandidateStatus(c, Candidate.CandidateStatus.SHORTLISTED);
            }
            progressListener.accept(String.format("Screened %d candidates. Prioritized top %d via Selection Sort.",
                    result.totalCandidatesProcessed, result.shortlistedCount));
            sleep(350);

            // Step 2: Queue / Priority Queue Interview Scheduling with Availability Check
            progressListener.accept("Processing interview queue with slot & room availability checks...");
            sleep(350);

            List<Employee> interviewers = employeeService.getEmployeesByDepartment(Department.ENGINEERING);
            if (interviewers.isEmpty()) {
                interviewers = employeeService.getAllEmployees();
            }

            // Schedule the top 8 candidates via PriorityQueue
            List<Candidate> toInterview = topRanked.subList(0, Math.min(8, topRanked.size()));
            List<QueueInterviewScheduler.InterviewBooking> bookings = schedulingService.autoSchedule(
                    toInterview, interviewers, LocalDate.now().plusDays(1), true
            );
            result.scheduledInterviewsCount = bookings.size();
            result.schedule = bookings;
            progressListener.accept(String.format("Scheduled %d zero-conflict interviews via PriorityQueue.",
                    result.scheduledInterviewsCount));
            sleep(350);

            // Step 3: Sweep-line Department Leave Bottleneck Detection
            progressListener.accept("Analyzing departmental absence overlaps using Sweep-Line intervals...");
            sleep(350);

            List<LeaveOverlapDetector.ConflictReport> conflicts = leaveService.detectConflicts(2);
            result.leaveConflictsDetected = conflicts.size();
            result.leaveConflicts = conflicts;

            progressListener.accept(String.format("Detected %d operational leave bottleneck alerts.",
                    result.leaveConflictsDetected));
            sleep(300);

            // Step 4: Prioritized Daily Task Dispatch via Priority Queue
            progressListener.accept("Formulating prioritized HR operational action items...");
            sleep(250);

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
                        "Queue-based schedule ready for tomorrow's panel sessions.",
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
