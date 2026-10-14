package com.kanban.dto;

import com.kanban.domain.entity.TaskColumn;
import com.kanban.domain.enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnResponseDto {

    private Long id;
    private String name;
    private TaskStatus statusKey;
    private Integer position;
    private String color;
    private Long boardId;
    private int taskCount;
    private List<TaskResponseDto> tasks = new ArrayList<>();

    public ColumnResponseDto() {
    }

    public static ColumnResponseDto fromEntity(TaskColumn column) {
        if (column == null) return null;
        ColumnResponseDto dto = new ColumnResponseDto();
        dto.setId(column.getId());
        dto.setName(column.getName());
        dto.setStatusKey(column.getStatusKey());
        dto.setPosition(column.getPosition());
        dto.setColor(column.getColor());
        if (column.getBoard() != null) {
            dto.setBoardId(column.getBoard().getId());
        }
        if (column.getTasks() != null) {
            dto.setTasks(column.getTasks().stream()
                    .map(TaskResponseDto::fromEntity)
                    .collect(Collectors.toList()));
            dto.setTaskCount(column.getTasks().size());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(TaskStatus statusKey) {
        this.statusKey = statusKey;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public List<TaskResponseDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponseDto> tasks) {
        this.tasks = tasks;
    }
}
