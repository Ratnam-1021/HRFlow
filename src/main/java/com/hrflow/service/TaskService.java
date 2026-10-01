package com.hrflow.service;

import com.hrflow.dsa.heap.TaskPriorityQueue;
import com.hrflow.model.HRTask;
import com.hrflow.storage.DatabaseManager;

import java.util.*;

/**
 * Service managing operational HR action items prioritized via MaxHeap.
 */
public class TaskService {
    private final DatabaseManager db;
    private final List<HRTask> tasks;
    private final TaskPriorityQueue priorityQueue;

    public TaskService() {
        this.db = DatabaseManager.getInstance();
        this.tasks = new ArrayList<>();
        this.priorityQueue = new TaskPriorityQueue();
        reload();
    }

    public synchronized void reload() {
        tasks.clear();
        tasks.addAll(db.loadAllHRTasks());
        rebuildQueue();
    }

    private void rebuildQueue() {
        // Rebuild priority heap with all pending tasks
        TaskPriorityQueue newQ = new TaskPriorityQueue();
        for (HRTask t : tasks) {
            if (t.getStatus() != HRTask.TaskStatus.COMPLETED && t.getStatus() != HRTask.TaskStatus.CANCELLED) {
                newQ.addTask(t);
            }
        }
    }

    public List<HRTask> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public List<HRTask> getPrioritizedPendingTasks(int limit) {
        TaskPriorityQueue q = new TaskPriorityQueue();
        for (HRTask t : tasks) {
            if (t.getStatus() != HRTask.TaskStatus.COMPLETED && t.getStatus() != HRTask.TaskStatus.CANCELLED) {
                q.addTask(t);
            }
        }
        return q.getTopUrgentTasks(limit);
    }

    public void addTask(HRTask task) {
        tasks.add(task);
        priorityQueue.addTask(task);
        db.insertHRTasks(List.of(task));
    }

    public void updateStatus(HRTask task, HRTask.TaskStatus status) {
        task.setStatus(status);
        rebuildQueue();
        db.insertHRTasks(List.of(task));
    }
}
