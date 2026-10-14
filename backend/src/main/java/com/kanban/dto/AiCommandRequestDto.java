package com.kanban.dto;

import com.kanban.domain.enums.TaskPriority;
import com.kanban.domain.enums.TaskStatus;

import java.util.Map;
import java.util.Set;

public class AiCommandRequestDto {

    /**
     * Action name, for example:
     * - "create_task"
     * - "mark_done"
     * - "move_task"
     * - "delete_task"
     * - "list_tasks"
     * - "natural_language"
     */
    private String action;

    // Optional natural language prompt, e.g.: "Create a task to build auth module with high priority"
    private String prompt;

    // Parameters for direct tool calls
    private Long taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Set<String> labels;
    private String assignee;
    private Long columnId;
    private Integer position;
    private Map<String, Object> extraParams;

    public AiCommandRequestDto() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }
}
