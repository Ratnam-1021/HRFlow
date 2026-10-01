package com.hrflow.dsa;

import com.hrflow.dsa.graph.InterviewScheduler;
import com.hrflow.dsa.heap.CandidateRankingHeap;
import com.hrflow.dsa.heap.MaxHeap;
import com.hrflow.dsa.interval.LeaveOverlapDetector;
import com.hrflow.dsa.search.BinarySearchEngine;
import com.hrflow.dsa.tree.OrgHierarchyTree;
import com.hrflow.dsa.trie.SkillTrie;
import com.hrflow.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DsaEngineTestSuite {

    @Test
    @DisplayName("MaxHeap: Correctly maintains max element and extracts top-K in descending order")
    void testMaxHeapAndCandidateRanking() {
        MaxHeap<Integer> heap = new MaxHeap<>();
        int[] numbers = {15, 42, 8, 99, 23, 77, 4};
        for (int n : numbers) heap.insert(n);

        assertEquals(99, heap.peek());
        assertEquals(99, heap.extractMax());
        assertEquals(77, heap.extractMax());

        // Test Candidate Ranking Heap
        Candidate c1 = new Candidate("C1", "Aman", "aman@ex.com", "Java Dev", Set.of("Java", "Spring"), 5.0, 90, 85, 80);
        Candidate c2 = new Candidate("C2", "Rahul", "rahul@ex.com", "Java Dev", Set.of("Python"), 1.0, 70, 60, 50);
        Candidate c3 = new Candidate("C3", "Priya", "priya@ex.com", "Java Dev", Set.of("Java", "Spring", "SQL"), 4.0, 95, 90, 85);

        CandidateRankingHeap ranking = CandidateRankingHeap.rankForRole(List.of(c1, c2, c3), Set.of("Java", "Spring"));
        List<Candidate> top2 = ranking.getTopK(2);

        assertEquals(2, top2.size());
        assertTrue(top2.get(0).getOverallScore() >= top2.get(1).getOverallScore());
        assertEquals("Aman", top2.get(0).getName());
    }

    @Test
    @DisplayName("SkillTrie: Fast prefix search and multi-skill candidate lookup")
    void testSkillTrie() {
        SkillTrie trie = new SkillTrie();
        Candidate c1 = new Candidate("C1", "Alice", "a@ex.com", "Dev", Set.of("Java", "JavaFX"), 3, 80, 80, 80);
        Candidate c2 = new Candidate("C2", "Bob", "b@ex.com", "Dev", Set.of("Java", "Spring Boot"), 4, 85, 85, 85);

        trie.insertSkill("Java", c1);
        trie.insertSkill("JavaFX", c1);
        trie.insertSkill("Java", c2);
        trie.insertSkill("Java Spring Boot", c2);
        trie.insertSkill("JavaScript", null);

        List<String> suggestions = trie.autocomplete("jav", 10);
        assertTrue(suggestions.contains("Java"));
        assertTrue(suggestions.contains("JavaFX"));
        assertTrue(suggestions.contains("JavaScript"));

        Set<Candidate> javaDevs = trie.getCandidatesBySkill("Java");
        assertEquals(2, javaDevs.size());

        Set<Candidate> multiMatches = trie.findCandidatesWithAllSkills(List.of("Java", "JavaFX"));
        assertEquals(1, multiMatches.size());
        assertTrue(multiMatches.contains(c1));
    }

    @Test
    @DisplayName("LeaveOverlapDetector: Detects concurrent departmental leave conflicts via Sweep-Line")
    void testLeaveOverlap() {
        LocalDate base = LocalDate.of(2026, 10, 10);
        LeaveRequest r1 = new LeaveRequest("L1", "E1", "Emp1", Department.ENGINEERING, base, base.plusDays(5), LeaveRequest.LeaveType.VACATION, "Trip");
        LeaveRequest r2 = new LeaveRequest("L2", "E2", "Emp2", Department.ENGINEERING, base.plusDays(2), base.plusDays(7), LeaveRequest.LeaveType.VACATION, "Family");
        LeaveRequest r3 = new LeaveRequest("L3", "E3", "Emp3", Department.ENGINEERING, base.plusDays(3), base.plusDays(6), LeaveRequest.LeaveType.VACATION, "Personal");

        LeaveOverlapDetector detector = new LeaveOverlapDetector();
        List<LeaveOverlapDetector.ConflictReport> conflicts = detector.analyzeConflicts(List.of(r1, r2, r3), 2);

        assertFalse(conflicts.isEmpty());
        assertEquals(Department.ENGINEERING, conflicts.get(0).getDepartment());
        assertEquals(3, conflicts.get(0).getPeakConcurrentLeaves());
    }

    @Test
    @DisplayName("OrgHierarchyTree: BFS level order, subtree headcount, and reporting chain")
    void testOrgHierarchy() {
        Employee ceo = new Employee("E-CEO", "Satya", "ceo@corp.com", Department.EXECUTIVE, "CEO", null, 300000, LocalDate.now(), 99, 98);
        Employee cto = new Employee("E-CTO", "Sundar", "cto@corp.com", Department.ENGINEERING, "CTO", "E-CEO", 220000, LocalDate.now(), 98, 95);
        Employee lead = new Employee("E-LEAD", "Linus", "lead@corp.com", Department.ENGINEERING, "Tech Lead", "E-CTO", 160000, LocalDate.now(), 97, 94);
        Employee dev = new Employee("E-DEV", "James", "dev@corp.com", Department.ENGINEERING, "Software Engineer", "E-LEAD", 110000, LocalDate.now(), 95, 90);

        OrgHierarchyTree tree = new OrgHierarchyTree();
        tree.buildTree(List.of(ceo, cto, lead, dev));

        assertEquals(4, tree.getTotalEmployees());
        assertEquals(2, tree.countSubtreeHeadcount("E-CTO")); // lead and dev reporting under CTO
        assertEquals(1, tree.countSubtreeHeadcount("E-LEAD")); // only dev

        List<Employee> chain = tree.getReportingChain("E-DEV");
        assertEquals(4, chain.size());
        assertEquals("James", chain.get(0).getName());
        assertEquals("Satya", chain.get(3).getName());
    }

    @Test
    @DisplayName("InterviewScheduler: Generates collision-free schedule using graph vertex coloring")
    void testInterviewScheduler() {
        Candidate c1 = new Candidate("C1", "Candidate 1", "c1@test.com", "Dev", Set.of("Java"), 3, 80, 80, 80);
        Candidate c2 = new Candidate("C2", "Candidate 2", "c2@test.com", "Dev", Set.of("Java"), 4, 85, 85, 85);
        Employee interviewerA = new Employee("I1", "Interviewer A", "ia@corp.com", Department.ENGINEERING, "Senior Dev", null, 120000, LocalDate.now(), 98, 90);

        // Both candidates need Interviewer A
        List<InterviewScheduler.ScheduledSession> requests = List.of(
                new InterviewScheduler.ScheduledSession(c1, interviewerA),
                new InterviewScheduler.ScheduledSession(c2, interviewerA)
        );

        InterviewScheduler scheduler = new InterviewScheduler();
        LocalDate date = LocalDate.of(2026, 10, 15);
        List<LocalTime> slots = List.of(LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(14, 0));
        List<String> rooms = List.of("Boardroom A", "Room 101");

        List<InterviewScheduler.InterviewBooking> bookings = scheduler.generateSchedule(requests, date, slots, rooms);

        assertEquals(2, bookings.size());
        // Since same interviewer is needed, they MUST be at different time slots!
        assertNotEquals(bookings.get(0).getTimeSlot(), bookings.get(1).getTimeSlot());
    }

    @Test
    @DisplayName("BinarySearchEngine: Verifies O(log N) search accuracy on sorted employee collection")
    void testBinarySearch() {
        List<Employee> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String id = String.format("EMP-%04d", i);
            list.add(new Employee(id, "Employee " + i, "emp" + i + "@corp.com", Department.ENGINEERING, "Engineer", "E-CTO", 80000 + i * 500, LocalDate.now(), 95, 85));
        }

        int index = BinarySearchEngine.binarySearchById(list, "EMP-0042");
        assertEquals(41, index);
        assertEquals("EMP-0042", list.get(index).getId());

        BinarySearchEngine.BenchmarkResult result = BinarySearchEngine.compareSearchPerformance(list, "EMP-0099");
        assertNotNull(result.getFoundEmployee());
        assertEquals("EMP-0099", result.getFoundEmployee().getId());
        assertTrue(result.getBinaryComparisons() <= 8); // log2(100) is ~7
    }
}
