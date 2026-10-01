package com.hrflow.service;

import com.hrflow.dsa.graph.InterviewScheduler;
import com.hrflow.model.Candidate;
import com.hrflow.model.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Service managing interview scheduling powered by graph vertex coloring.
 */
public class SchedulingService {
    private final InterviewScheduler scheduler;
    private final List<InterviewScheduler.InterviewBooking> currentSchedule;

    public SchedulingService() {
        this.scheduler = new InterviewScheduler();
        this.currentSchedule = new ArrayList<>();
    }

    public List<InterviewScheduler.InterviewBooking> getCurrentSchedule() {
        return Collections.unmodifiableList(currentSchedule);
    }

    /**
     * Solves interview allocations using graph conflict coloring.
     */
    public List<InterviewScheduler.InterviewBooking> autoSchedule(List<Candidate> candidates,
                                                                 List<Employee> interviewers,
                                                                 LocalDate date) {
        currentSchedule.clear();
        if (candidates == null || candidates.isEmpty() || interviewers == null || interviewers.isEmpty()) {
            return currentSchedule;
        }

        List<InterviewScheduler.ScheduledSession> sessions = new ArrayList<>();
        int interviewerIdx = 0;

        for (Candidate c : candidates) {
            // Assign candidate to interviewer
            Employee interviewer = interviewers.get(interviewerIdx % interviewers.size());
            sessions.add(new InterviewScheduler.ScheduledSession(c, interviewer));
            interviewerIdx++;
        }

        List<LocalTime> slots = List.of(
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                LocalTime.of(11, 30),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0)
        );

        List<String> rooms = List.of("Boardroom Alpha", "Apollo Suite", "Turing Lab", "Nexus Conference");

        List<InterviewScheduler.InterviewBooking> bookings = scheduler.generateSchedule(sessions, date, slots, rooms);
        currentSchedule.addAll(bookings);

        // Update candidate statuses
        for (Candidate c : candidates) {
            c.setStatus(Candidate.CandidateStatus.INTERVIEW_SCHEDULED);
        }

        return currentSchedule;
    }
}
