package com.hrflow.dsa.queue;

import com.hrflow.model.Candidate;
import com.hrflow.model.Interviewer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Queue-based Interview Scheduler with Simple Availability Checking.
 * - Queue / PriorityQueue for candidate interview processing
 * - Availability check for slots, interviewers, and rooms.
 */
public class QueueInterviewScheduler {

    public static class InterviewBooking {
        private final String id;
        private final Candidate candidate;
        private final Interviewer interviewer;
        private final LocalDate date;
        private final LocalTime timeSlot;
        private final String room;

        public InterviewBooking(String id, Candidate candidate, Interviewer interviewer,
                                LocalDate date, LocalTime timeSlot, String room) {
            this.id = id;
            this.candidate = candidate;
            this.interviewer = interviewer;
            this.date = date;
            this.timeSlot = timeSlot;
            this.room = room;
        }

        public String getId() { return id; }
        public Candidate getCandidate() { return candidate; }
        public Interviewer getInterviewer() { return interviewer; }
        public LocalDate getDate() { return date; }
        public LocalTime getTimeSlot() { return timeSlot; }
        public String getRoom() { return room; }

        @Override
        public String toString() {
            return String.format("[%s] %s with %s in %s", timeSlot, candidate.getName(), interviewer.getName(), room);
        }
    }

    public static class ScheduledSession implements Comparable<ScheduledSession> {
        public final Candidate candidate;
        public final Interviewer interviewer;

        public ScheduledSession(Candidate candidate, Interviewer interviewer) {
            this.candidate = candidate;
            this.interviewer = interviewer;
        }

        @Override
        public int compareTo(ScheduledSession other) {
            // Higher candidate score gets scheduled first in Priority Queue
            return Double.compare(other.candidate.getOverallScore(), this.candidate.getOverallScore());
        }
    }

    /**
     * Schedules interviews using a Queue (FIFO or PriorityQueue) and simple availability checking.
     *
     * @param requests Candidate-interviewer pairs to schedule
     * @param usePriorityQueue If true, higher scoring candidates are scheduled first using PriorityQueue
     */
    public List<InterviewBooking> scheduleInterviews(List<ScheduledSession> requests,
                                                    LocalDate interviewDate,
                                                    List<LocalTime> availableSlots,
                                                    List<String> rooms,
                                                    boolean usePriorityQueue) {
        List<InterviewBooking> confirmedBookings = new ArrayList<>();
        if (requests == null || requests.isEmpty() || availableSlots.isEmpty() || rooms.isEmpty()) {
            return confirmedBookings;
        }

        // Initialize Queue: PriorityQueue (by candidate score) or LinkedList (FIFO Queue)
        Queue<ScheduledSession> sessionQueue;
        if (usePriorityQueue) {
            sessionQueue = new PriorityQueue<>(requests);
        } else {
            sessionQueue = new LinkedList<>(requests);
        }

        // Availability Tracking using HashSets
        Set<String> busyInterviewers = new HashSet<>();
        Set<String> busyCandidates = new HashSet<>();
        Set<String> occupiedRooms = new HashSet<>();

        int bookingIdCounter = 1;

        while (!sessionQueue.isEmpty()) {
            ScheduledSession session = sessionQueue.poll();
            boolean scheduled = false;

            for (LocalTime slot : availableSlots) {
                String slotKey = slot.toString();
                String interviewerKey = session.interviewer.getId() + "@" + slotKey;
                String candidateKey = session.candidate.getId() + "@" + slotKey;

                // Availability check: interviewer or candidate already booked?
                if (busyInterviewers.contains(interviewerKey) || busyCandidates.contains(candidateKey)) {
                    continue;
                }

                // Availability check: any room free at this slot?
                for (String room : rooms) {
                    String roomKey = room + "@" + slotKey;
                    if (!occupiedRooms.contains(roomKey)) {
                        busyInterviewers.add(interviewerKey);
                        busyCandidates.add(candidateKey);
                        occupiedRooms.add(roomKey);

                        confirmedBookings.add(new InterviewBooking(
                                "INT-" + (bookingIdCounter++),
                                session.candidate,
                                session.interviewer,
                                interviewDate,
                                slot,
                                room
                        ));
                        scheduled = true;
                        break;
                    }
                }

                if (scheduled) {
                    break;
                }
            }
        }

        confirmedBookings.sort(Comparator.comparing(InterviewBooking::getTimeSlot).thenComparing(InterviewBooking::getRoom));
        return confirmedBookings;
    }
}
