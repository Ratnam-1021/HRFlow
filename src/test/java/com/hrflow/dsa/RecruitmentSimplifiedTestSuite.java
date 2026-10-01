package com.hrflow.dsa;

import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.dsa.search.SkillSearchManager;
import com.hrflow.dsa.sort.CandidateSortEngine;
import com.hrflow.dsa.stack.HRActionStack;
import com.hrflow.model.Candidate;
import com.hrflow.model.Employee;
import com.hrflow.model.HRAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RecruitmentSimplifiedTestSuite {

    @Test
    @DisplayName("Pillar 1: HashMap + Linear Search for Skill Autocomplete and Candidate Lookup")
    void testHashMapAndLinearSearch() {
        SkillSearchManager manager = new SkillSearchManager();
        Candidate c1 = new Candidate("C1", "Alice", "a@test.com", "Dev", Set.of("Java", "JavaFX"), 3, 80, 80, 80);
        Candidate c2 = new Candidate("C2", "Bob", "b@test.com", "Dev", Set.of("Java", "Spring Boot"), 4, 85, 85, 85);

        manager.registerSkill("Java", c1);
        manager.registerSkill("JavaFX", c1);
        manager.registerSkill("Java", c2);
        manager.registerSkill("Spring Boot", c2);

        // Linear Search autocomplete
        List<String> suggestions = manager.autocomplete("jav", 10);
        assertTrue(suggestions.contains("Java"));
        assertTrue(suggestions.contains("JavaFX"));
        assertFalse(suggestions.contains("Spring Boot"));

        // HashMap O(1) candidate lookup
        Set<Candidate> javaDevs = manager.getCandidatesBySkill("Java");
        assertEquals(2, javaDevs.size());

        // Multi-skill nested loop matching
        Set<Candidate> multi = manager.findCandidatesWithAllSkills(List.of("Java", "JavaFX"), List.of(c1, c2));
        assertEquals(1, multi.size());
        assertTrue(multi.contains(c1));
    }

    @Test
    @DisplayName("Pillar 1: Candidate Ranking via Selection Sort (Top-K) and Insertion Sort")
    void testSelectionAndInsertionSort() {
        Candidate c1 = new Candidate("C1", "Aman", "aman@ex.com", "Dev", Set.of("Java"), 5.0, 90, 85, 80);
        Candidate c2 = new Candidate("C2", "Rahul", "rahul@ex.com", "Dev", Set.of("Java"), 1.0, 70, 60, 50);
        Candidate c3 = new Candidate("C3", "Priya", "priya@ex.com", "Dev", Set.of("Java"), 4.0, 95, 90, 85);

        // 1. Selection Sort for Top-K
        List<Candidate> top2 = CandidateSortEngine.getTopKViaSelectionSort(List.of(c1, c2, c3), 2);
        assertEquals(2, top2.size());
        assertTrue(top2.get(0).getOverallScore() >= top2.get(1).getOverallScore());
        assertEquals("Aman", top2.get(0).getName());

        // 2. Insertion Sort for all candidates
        List<Candidate> sorted = CandidateSortEngine.sortByInsertionSort(List.of(c2, c1, c3));
        assertEquals(3, sorted.size());
        assertEquals("Aman", sorted.get(0).getName());
        assertEquals("Priya", sorted.get(1).getName());
        assertEquals("Rahul", sorted.get(2).getName());
    }

    @Test
    @DisplayName("Pillar 2: Queue & PriorityQueue Scheduling with Availability Check")
    void testQueueInterviewScheduler() {
        Candidate c1 = new Candidate("C1", "Candidate 1", "c1@test.com", "Dev", Set.of("Java"), 3, 80, 80, 80);
        Candidate c2 = new Candidate("C2", "Candidate 2", "c2@test.com", "Dev", Set.of("Java"), 4, 85, 85, 85);
        Employee interviewerA = new Employee("I1", "Interviewer A", "ia@corp.com", null, "Senior Dev", null, 120000, LocalDate.now(), 98, 90);

        List<QueueInterviewScheduler.ScheduledSession> requests = List.of(
                new QueueInterviewScheduler.ScheduledSession(c1, interviewerA),
                new QueueInterviewScheduler.ScheduledSession(c2, interviewerA)
        );

        QueueInterviewScheduler scheduler = new QueueInterviewScheduler();
        LocalDate date = LocalDate.of(2026, 10, 15);
        List<LocalTime> slots = List.of(LocalTime.of(10, 0), LocalTime.of(11, 0));
        List<String> rooms = List.of("Boardroom Alpha");

        List<QueueInterviewScheduler.InterviewBooking> bookings = scheduler.scheduleInterviews(
                requests, date, slots, rooms, true
        );

        assertEquals(2, bookings.size());
        // Since same interviewer is needed, they MUST be at different slots
        assertNotEquals(bookings.get(0).getTimeSlot(), bookings.get(1).getTimeSlot());
    }

    @Test
    @DisplayName("Pillar 3: Stack (Undo and Redo) for Candidate Actions")
    void testStackUndoAndRedo() {
        HRActionStack stack = new HRActionStack();
        Candidate c = new Candidate("C10", "Dev Test", "d@test.com", "Engineer", Set.of("Java"), 2, 70, 70, 70);
        assertEquals(Candidate.CandidateStatus.APPLIED, c.getStatus());

        // Action: Shortlist candidate
        HRAction action = new HRAction(
                "ACT-1",
                HRAction.ActionType.SHORTLIST,
                "Shortlisted Dev Test",
                () -> c.setStatus(Candidate.CandidateStatus.APPLIED), // Undo logic
                () -> c.setStatus(Candidate.CandidateStatus.SHORTLISTED) // Redo logic
        );
        c.setStatus(Candidate.CandidateStatus.SHORTLISTED);
        stack.pushAction(action);

        assertTrue(stack.canUndo());
        assertFalse(stack.canRedo());
        assertEquals(Candidate.CandidateStatus.SHORTLISTED, c.getStatus());

        // Perform Undo (Stack Pop)
        HRAction undone = stack.undo();
        assertNotNull(undone);
        assertEquals(Candidate.CandidateStatus.APPLIED, c.getStatus());
        assertTrue(stack.canRedo());

        // Perform Redo
        HRAction redone = stack.redo();
        assertNotNull(redone);
        assertEquals(Candidate.CandidateStatus.SHORTLISTED, c.getStatus());
    }
}
