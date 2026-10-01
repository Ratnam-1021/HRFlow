package com.hrflow.dsa.graph;

import com.hrflow.model.Candidate;
import com.hrflow.model.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Interview Scheduling engine using Graph Modeling and Greedy Vertex Coloring.
 *
 * Conflict Model:
 * - Each Interview Request is a Vertex V_i.
 * - An undirected edge exists between V_i and V_j if they share the same Candidate or Interviewer.
 * - Graph Coloring assigns non-conflicting time slots (colors).
 * - Room Assignment greedily maps scheduled slots into physical interview rooms.
 */
public class InterviewScheduler {

    public static class InterviewBooking {
        private final String id;
        private final Candidate candidate;
        private final Employee interviewer;
        private final LocalDate date;
        private final LocalTime timeSlot;
        private final String room;

        public InterviewBooking(String id, Candidate candidate, Employee interviewer,
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
        public Employee getInterviewer() { return interviewer; }
        public LocalDate getDate() { return date; }
        public LocalTime getTimeSlot() { return timeSlot; }
        public String getRoom() { return room; }

        @Override
        public String toString() {
            return String.format("[%s] %s with %s in %s", timeSlot, candidate.getName(), interviewer.getName(), room);
        }
    }

    public static class ScheduledSession {
        public final Candidate candidate;
        public final Employee interviewer;

        public ScheduledSession(Candidate candidate, Employee interviewer) {
            this.candidate = candidate;
            this.interviewer = interviewer;
        }
    }

    /**
     * Schedules interviews without collisions using Graph Coloring & Greedy Room Assignment.
     */
    public List<InterviewBooking> generateSchedule(List<ScheduledSession> requests,
                                                   LocalDate interviewDate,
                                                   List<LocalTime> availableSlots,
                                                   List<String> rooms) {
        List<InterviewBooking> bookings = new ArrayList<>();
        if (requests == null || requests.isEmpty() || availableSlots.isEmpty() || rooms.isEmpty()) {
            return bookings;
        }

        int n = requests.size();
        // Step 1: Build Adjacency Matrix for Conflict Graph
        boolean[][] conflictGraph = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                ScheduledSession r1 = requests.get(i);
                ScheduledSession r2 = requests.get(j);

                boolean sameCandidate = r1.candidate.getId().equals(r2.candidate.getId());
                boolean sameInterviewer = r1.interviewer.getId().equals(r2.interviewer.getId());

                if (sameCandidate || sameInterviewer) {
                    conflictGraph[i][j] = true;
                    conflictGraph[j][i] = true;
                }
            }
        }

        // Step 2: Greedy Welsh-Powell / Degree-ordered Graph Coloring
        // Calculate degrees
        int[] degrees = new int[n];
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            for (int j = 0; j < n; j++) {
                if (conflictGraph[i][j]) degrees[i]++;
            }
        }

        // Sort vertices by descending degree (heuristic for optimal coloring)
        Arrays.sort(order, (a, b) -> Integer.compare(degrees[b], degrees[a]));

        int[] colors = new int[n];
        Arrays.fill(colors, -1);

        for (int u : order) {
            boolean[] usedColors = new boolean[availableSlots.size()];
            for (int v = 0; v < n; v++) {
                if (conflictGraph[u][v] && colors[v] != -1) {
                    if (colors[v] < usedColors.length) {
                        usedColors[colors[v]] = true;
                    }
                }
            }

            int chosenColor = -1;
            for (int c = 0; c < usedColors.length; c++) {
                if (!usedColors[c]) {
                    chosenColor = c;
                    break;
                }
            }

            colors[u] = (chosenColor != -1) ? chosenColor : (u % availableSlots.size());
        }

        // Step 3: Greedy Room Allocation per Slot
        Map<Integer, List<Integer>> slotToSessions = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            int slotIdx = colors[i] % availableSlots.size();
            slotToSessions.computeIfAbsent(slotIdx, k -> new ArrayList<>()).add(i);
        }

        int bookingCounter = 1;
        for (Map.Entry<Integer, List<Integer>> entry : slotToSessions.entrySet()) {
            int slotIdx = entry.getKey();
            LocalTime slotTime = availableSlots.get(slotIdx);
            List<Integer> sessionIndices = entry.getValue();

            int roomIdx = 0;
            for (int idx : sessionIndices) {
                ScheduledSession session = requests.get(idx);
                String room = rooms.get(roomIdx % rooms.size());
                bookings.add(new InterviewBooking(
                        "INT-" + (bookingCounter++),
                        session.candidate,
                        session.interviewer,
                        interviewDate,
                        slotTime,
                        room
                ));
                roomIdx++;
            }
        }

        // Sort bookings by time slot then room
        bookings.sort(Comparator.comparing(InterviewBooking::getTimeSlot).thenComparing(InterviewBooking::getRoom));
        return bookings;
    }
}
