package com.hrflow.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an action tracked on the LIFO Stack supporting both Undo and Redo.
 */
public class HRAction {
    public enum ActionType {
        SHORTLIST,
        REJECT,
        STATUS_CHANGE,
        SCHEDULE_INTERVIEW,
        FILTER_APPLIED
    }

    private final String id;
    private final ActionType type;
    private final String description;
    private final LocalDateTime timestamp;
    private final Runnable undoHandler;
    private final Runnable redoHandler;

    public HRAction(String id, ActionType type, String description, Runnable undoHandler) {
        this(id, type, description, undoHandler, null);
    }

    public HRAction(String id, ActionType type, String description, Runnable undoHandler, Runnable redoHandler) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.undoHandler = undoHandler;
        this.redoHandler = redoHandler;
    }

    public String getId() { return id; }
    public ActionType getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void executeUndo() {
        if (undoHandler != null) {
            undoHandler.run();
        }
    }

    public void executeRedo() {
        if (redoHandler != null) {
            redoHandler.run();
        }
    }

    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getFormattedTime(), type, description);
    }
}
