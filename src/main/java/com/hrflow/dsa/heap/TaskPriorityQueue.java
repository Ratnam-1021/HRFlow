package com.hrflow.dsa.heap;

import com.hrflow.model.HRTask;

import java.util.Collection;
import java.util.List;

/**
 * Priority Queue for automated HR operational task dispatch.
 * Powered by custom MaxHeap.
 */
public class TaskPriorityQueue {
    private final MaxHeap<HRTask> heap;

    public TaskPriorityQueue() {
        this.heap = new MaxHeap<>();
    }

    public void addTask(HRTask task) {
        heap.insert(task);
    }

    public void addAll(Collection<HRTask> tasks) {
        for (HRTask t : tasks) {
            heap.insert(t);
        }
    }

    public HRTask peekMostUrgent() {
        return heap.peek();
    }

    public HRTask pollMostUrgent() {
        return heap.extractMax();
    }

    public List<HRTask> getTopUrgentTasks(int k) {
        return heap.extractTopK(k);
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}
