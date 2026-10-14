package com.kanban.dto;

import com.kanban.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskMoveDto {

    private Long targetColumnId;

    private TaskStatus targetStatus;

    @NotNull(message = "Target position is required")
    private Integer targetPosition;

    public TaskMoveDto() {
    }

    public TaskMoveDto(Long targetColumnId, Integer targetPosition) {
        this.targetColumnId = targetColumnId;
        this.targetPosition = targetPosition;
    }

    public Long getTargetColumnId() {
        return targetColumnId;
    }

    public void setTargetColumnId(Long targetColumnId) {
        this.targetColumnId = targetColumnId;
    }

    public TaskStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(TaskStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public Integer getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Integer targetPosition) {
        this.targetPosition = targetPosition;
    }
}
