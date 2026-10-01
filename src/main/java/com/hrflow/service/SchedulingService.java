package com.hrflow.service;

import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.model.Candidate;
import com.hrflow.model.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Service managing interview scheduling powered by Queue & PriorityQueue
 * with simple availability checking.
 */
public class SchedulingService {
    private final QueueInterviewScheduler scheduler;
    private final List<QueueInterviewScheduler.InterviewBooking> currentSchedule;

    public SchedulingService() {
        this.scheduler = new QueueInterviewScheduler();
        this.currentSchedule = new ArrayList<>();
    }

    public List<QueueInterviewScheduler.InterviewBooking> getCurrentSchedule() {
        return Collections.unmodifiableList(currentSchedule);
    }

    /**
     * Solves interview allocations using Queue-based scheduling (FIFO or PriorityQueue)
     * and slot/room availability tracking.
     */
    public List<QueueInterviewScheduler.InterviewBooking> autoSchedule(List<Candidate> candidates,
                                                                      List<Employee> interviewers,
                                                                      LocalDate date) {
        return autoSchedule(candidates, interviewers, date, true); // default to PriorityQueue by score
    }

    public List<QueueInterviewScheduler.InterviewBooking> autoSchedule(List<Candidate> candidates,
                                                                      List<Employee> interviewers,
                                                                      LocalDate date,
                                                                      boolean usePriorityQueue) {
        currentSchedule.clear();
        if (candidates == null || candidates.isEmpty() || interviewers == null || interviewers.isEmpty()) {
            return currentSchedule;
        }

        List<QueueInterviewScheduler.ScheduledSession> sessions = new ArrayList<>();
        int interviewerIdx = 0;

        for (Candidate c : candidates) {
            Employee interviewer = interviewers.get(interviewerIdx % interviewers.size());
            sessions.add(new QueueInterviewScheduler.ScheduledSession(c, interviewer));
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

        List<QueueInterviewScheduler.InterviewBooking> bookings = scheduler.scheduleInterviews(
                sessions, date, slots, rooms, usePriorityQueue
        );
        currentSchedule.addAll(bookings);

        for (Candidate c : candidates) {
            c.setStatus(Candidate.CandidateStatus.INTERVIEW_SCHEDULED);
        }

        return currentSchedule;
    }
}
