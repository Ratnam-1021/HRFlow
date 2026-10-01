package com.hrflow.dsa.stack;

import com.hrflow.model.HRAction;

import java.util.*;

/**
 * Dual-Stack Architecture for HR Action History, Undo, and Redo.
 * 
 * - undoStack: Stores actions that can be reverted (LIFO).
 * - redoStack: Stores undone actions that can be re-applied (LIFO).
 * - historyAuditLog: Chronological audit trail for HR monitoring.
 */
public class HRActionStack {
    private final Deque<HRAction> undoStack;
    private final Deque<HRAction> redoStack;
    private final List<HRAction> historyAuditLog;
    private final int maxCapacity;

    public HRActionStack() {
        this(100);
    }

    public HRActionStack(int maxCapacity) {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
        this.historyAuditLog = new ArrayList<>();
        this.maxCapacity = maxCapacity;
    }

    /**
     * Pushes a new action onto the Undo Stack and clears the Redo Stack.
     */
    public synchronized void pushAction(HRAction action) {
        if (action == null) return;

        if (undoStack.size() >= maxCapacity) {
            undoStack.removeLast(); // drop oldest action
        }

        undoStack.push(action);
        redoStack.clear(); // Standard UX: new action invalidates previous redo history
        historyAuditLog.add(0, action); // newest at index 0
    }

    /**
     * Pops action from Undo Stack, executes reverse logic, and pushes to Redo Stack.
     */
    public synchronized HRAction undo() {
        if (undoStack.isEmpty()) {
            return null;
        }

        HRAction action = undoStack.pop();
        action.executeUndo();
        redoStack.push(action);
        return action;
    }

    /**
     * Pops action from Redo Stack, re-executes action, and pushes back to Undo Stack.
     */
    public synchronized HRAction redo() {
        if (redoStack.isEmpty()) {
            return null;
        }

        HRAction action = redoStack.pop();
        action.executeRedo();
        undoStack.push(action);
        return action;
    }

    public synchronized HRAction peekUndo() {
        return undoStack.peek();
    }

    public synchronized HRAction peekRedo() {
        return redoStack.peek();
    }

    public synchronized boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public synchronized boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public synchronized int undoSize() {
        return undoStack.size();
    }

    public synchronized int redoSize() {
        return redoStack.size();
    }

    public synchronized List<HRAction> getHistoryAuditLog() {
        return Collections.unmodifiableList(historyAuditLog);
    }

    public synchronized void clear() {
        undoStack.clear();
        redoStack.clear();
        historyAuditLog.clear();
    }
}
