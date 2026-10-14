package com.kanban.controller;

import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.TaskCreateDto;
import com.kanban.dto.TaskMoveDto;
import com.kanban.dto.TaskResponseDto;
import com.kanban.dto.TaskUpdateDto;
import com.kanban.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long columnId) {

        if (query != null && !query.trim().isEmpty()) {
            return ResponseEntity.ok(taskService.searchTasks(query));
        }
        if (columnId != null) {
            return ResponseEntity.ok(taskService.getTasksByColumn(columnId));
        }
        if (status != null) {
            return ResponseEntity.ok(taskService.getTasksByStatus(status));
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateDto createDto) {
        TaskResponseDto created = taskService.createTask(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto updateDto) {
        return ResponseEntity.ok(taskService.updateTask(id, updateDto));
    }

    @Override
    @PatchMapping("/{id}/move")
    public ResponseEntity<TaskResponseDto> moveTask(@PathVariable Long id, @Valid @RequestBody TaskMoveDto moveDto) {
        return ResponseEntity.ok(taskService.moveTask(id, moveDto));
    }

    @Override
    @PatchMapping("/{id}/done")
    public ResponseEntity<TaskResponseDto> markTaskAsDone(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markAsDone(id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
