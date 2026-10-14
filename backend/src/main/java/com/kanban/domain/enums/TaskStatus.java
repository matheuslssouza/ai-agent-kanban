package com.kanban.domain.enums;

public enum TaskStatus {
    BACKLOG,
    TODO,
    IN_PROGRESS,
    IN_REVIEW,
    DONE;

    public static TaskStatus fromString(String value) {
        if (value == null) {
            return TODO;
        }
        try {
            return TaskStatus.valueOf(value.trim().toUpperCase().replace("-", "_").replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return TODO;
        }
    }
}
