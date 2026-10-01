package com.hrflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * HR operational action items prioritized using a Priority Queue / Heap.
 */
public class HRTask implements Comparable<HRTask> {
    public enum Priority {
        CRITICAL(4),
        HIGH(3),
        MEDIUM(2),
        LOW(1);

        private final int level;
        Priority(int level) { this.level = level; }
        public int getLevel() { return level; }
    }

    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    private final String id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate deadline;
    private Department department;
    private TaskStatus status;
    private LocalDateTime createdAt;

    public HRTask(String id, String title, String description, Priority priority,
                  LocalDate deadline, Department department) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.priority = (priority != null) ? priority : Priority.MEDIUM;
        this.deadline = (deadline != null) ? deadline : LocalDate.now().plusDays(3);
        this.department = department;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public int compareTo(HRTask other) {
        // In MaxHeap, higher priority level is greater
        int priorityCmp = Integer.compare(this.priority.getLevel(), other.priority.getLevel());
        if (priorityCmp != 0) {
            return priorityCmp;
        }
        // Earlier deadline has higher urgency
        return other.deadline.compareTo(this.deadline);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HRTask hrTask)) return false;
        return Objects.equals(id, hrTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
