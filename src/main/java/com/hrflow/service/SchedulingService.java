package com.hrflow.service;

import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.model.Candidate;
import com.hrflow.model.Interviewer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Service managing interview scheduling strictly for Recruitment & Hiring.
 * Powered by Queue & PriorityQueue with Availability Checking.
 */
public class SchedulingService {
    private final QueueInterviewScheduler scheduler;
    private final List<QueueInterviewScheduler.InterviewBooking> currentSchedule;
    private final List<Interviewer> interviewPanel;

    public SchedulingService() {
        this.scheduler = new QueueInterviewScheduler();
        this.currentSchedule = new ArrayList<>();
        this.interviewPanel = new ArrayList<>(List.of(
                new Interviewer("INT-P1", "Alex Rivers", "Lead Architect", "alex.r@hrflow.corp"),
                new Interviewer("INT-P2", "Sarah Connor", "Principal Engineer", "sarah.c@hrflow.corp"),
                new Interviewer("INT-P3", "David Chen", "Senior Java Specialist", "david.c@hrflow.corp"),
                new Interviewer("INT-P4", "Elena Rostova", "Staff Platform Engineer", "elena.r@hrflow.corp")
        ));
    }

    public List<QueueInterviewScheduler.InterviewBooking> getCurrentSchedule() {
        return Collections.unmodifiableList(currentSchedule);
    }

    public List<Interviewer> getInterviewPanel() {
        return Collections.unmodifiableList(interviewPanel);
    }

    /**
     * Solves interview allocations using Queue-based scheduling (FIFO or PriorityQueue)
     * and slot/room availability tracking.
     */
    public List<QueueInterviewScheduler.InterviewBooking> autoSchedule(List<Candidate> candidates, LocalDate date) {
        return autoSchedule(candidates, interviewPanel, date, true);
    }

    public List<QueueInterviewScheduler.InterviewBooking> autoSchedule(List<Candidate> candidates,
                                                                      List<Interviewer> interviewers,
                                                                      LocalDate date,
                                                                      boolean usePriorityQueue) {
        currentSchedule.clear();
        if (candidates == null || candidates.isEmpty()) {
            return currentSchedule;
        }

        List<Interviewer> activeInterviewers = (interviewers == null || interviewers.isEmpty()) ? interviewPanel : interviewers;
        List<QueueInterviewScheduler.ScheduledSession> sessions = new ArrayList<>();
        int interviewerIdx = 0;

        for (Candidate c : candidates) {
            Interviewer interviewer = activeInterviewers.get(interviewerIdx % activeInterviewers.size());
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
