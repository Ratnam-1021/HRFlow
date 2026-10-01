package com.hrflow.service;

import com.hrflow.app.AppContext;
import com.hrflow.dsa.graph.InterviewScheduler;
import com.hrflow.dsa.interval.LeaveOverlapDetector;
import com.hrflow.dsa.search.BinarySearchEngine;
import com.hrflow.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class EndToEndIntegrationTest {

    private static AppContext context;

    @BeforeAll
    static void setup() {
        context = AppContext.getInstance();
    }

    @Test
    @DisplayName("End-to-End: Database seeds candidates, employees, leaves, and tasks")
    void testDatabaseSeeding() {
        List<Candidate> candidates = context.getCandidateService().getAllCandidates();
        List<Employee> employees = context.getEmployeeService().getAllEmployees();
        List<LeaveRequest> leaves = context.getLeaveService().getAllLeaveRequests();
        List<HRTask> tasks = context.getTaskService().getAllTasks();

        assertTrue(candidates.size() >= 200, "Should have seeded 200+ candidates");
        assertTrue(employees.size() >= 15, "Should have seeded 15+ employees");
        assertFalse(leaves.isEmpty(), "Should have seeded leave requests");
        assertFalse(tasks.isEmpty(), "Should have seeded tasks");
    }

    @Test
    @DisplayName("End-to-End: Trie autocomplete & Inverted skill indexing")
    void testSkillTrieWorkflow() {
        List<String> javaSuggestions = context.getCandidateService().autocompleteSkills("jav", 5);
        assertFalse(javaSuggestions.isEmpty());
        assertTrue(javaSuggestions.stream().anyMatch(s -> s.toLowerCase().startsWith("jav")));

        Set<Candidate> javaCandidates = context.getCandidateService().searchCandidatesBySkill("Java");
        assertFalse(javaCandidates.isEmpty());
    }

    @Test
    @DisplayName("End-to-End: Candidate Ranking Heap retrieves top K")
    void testCandidateRankingHeap() {
        List<Candidate> top10 = context.getCandidateService().rankCandidates(Set.of("Java", "Spring Boot", "SQL"), 10);
        assertEquals(10, top10.size());

        for (int i = 0; i < top10.size() - 1; i++) {
            assertTrue(top10.get(i).getOverallScore() >= top10.get(i + 1).getOverallScore(),
                    "Candidates must be in descending order of score");
        }
    }

    @Test
    @DisplayName("End-to-End: Sweep-line leave conflict analysis detects engineered bottleneck")
    void testSweepLineConflict() {
        List<LeaveOverlapDetector.ConflictReport> conflicts = context.getLeaveService().detectConflicts(2);
        assertFalse(conflicts.isEmpty(), "Must detect engineered 3-person Engineering overlap");
        assertEquals(Department.ENGINEERING, conflicts.get(0).getDepartment());
    }

    @Test
    @DisplayName("End-to-End: Binary search benchmark provides logarithmic efficiency")
    void testBinarySearchBenchmark() {
        List<Employee> emps = context.getEmployeeService().getAllEmployees();
        Employee target = emps.get(emps.size() - 1);
        BinarySearchEngine.BenchmarkResult res = context.getEmployeeService().benchmarkSearch(target.getId());

        assertNotNull(res.getFoundEmployee());
        assertEquals(target.getId(), res.getFoundEmployee().getId());
        assertTrue(res.getBinaryComparisons() < res.getLinearComparisons(),
                "Binary search comparisons (" + res.getBinaryComparisons() + ") must be less than linear (" + res.getLinearComparisons() + ")");
    }

    @Test
    @DisplayName("End-to-End: Daily Automation Center completes full pipeline asynchronously")
    void testDailyAutomationCenter() throws ExecutionException, InterruptedException {
        CompletableFuture<AutomationCenterService.AutomationResult> future =
                context.getAutomationCenterService().runDailyAutomationAsync(msg -> {
                    // System.out.println("[PROGRESS] " + msg);
                });

        AutomationCenterService.AutomationResult res = future.get();
        assertNotNull(res);
        assertTrue(res.totalCandidatesProcessed >= 200);
        assertEquals(15, res.shortlistedCount);
        assertEquals(8, res.scheduledInterviewsCount);
        assertTrue(res.leaveConflictsDetected >= 1);
        assertTrue(res.urgentTasksCreated >= 1);
    }
}
