package com.kanban.controller;

import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.TaskCreateDto;
import com.kanban.dto.TaskMoveDto;
import com.kanban.dto.TaskResponseDto;
import com.kanban.dto.TaskUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Tasks", description = "Endpoints for managing Kanban tasks/cards")
public interface TaskControllerApi {

    @Operation(summary = "List all tasks with optional search query or status filter")
    ResponseEntity<List<TaskResponseDto>> getTasks(String query, TaskStatus status, Long columnId);

    @Operation(summary = "Get task by ID")
    ResponseEntity<TaskResponseDto> getTaskById(Long id);

    @Operation(summary = "Create a new task")
    ResponseEntity<TaskResponseDto> createTask(TaskCreateDto createDto);

    @Operation(summary = "Update an existing task")
    ResponseEntity<TaskResponseDto> updateTask(Long id, TaskUpdateDto updateDto);

    @Operation(summary = "Move task to another column or position (drag and drop)")
    ResponseEntity<TaskResponseDto> moveTask(Long id, TaskMoveDto moveDto);

    @Operation(summary = "Quickly mark task as DONE")
    ResponseEntity<TaskResponseDto> markTaskAsDone(Long id);

    @Operation(summary = "Delete task by ID")
    ResponseEntity<Void> deleteTask(Long id);
}
