package com.kanban.service;

import com.kanban.domain.enums.TaskPriority;
import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.TaskCreateDto;
import com.kanban.dto.TaskMoveDto;
import com.kanban.dto.TaskResponseDto;
import com.kanban.dto.TaskUpdateDto;

import java.util.List;

public interface TaskService {

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto getTaskById(Long id);

    List<TaskResponseDto> getTasksByColumn(Long columnId);

    List<TaskResponseDto> getTasksByStatus(TaskStatus status);

    List<TaskResponseDto> searchTasks(String query);

    TaskResponseDto createTask(TaskCreateDto createDto);

    TaskResponseDto updateTask(Long id, TaskUpdateDto updateDto);

    TaskResponseDto moveTask(Long id, TaskMoveDto moveDto);

    TaskResponseDto markAsDone(Long id);

    TaskResponseDto updateTaskStatus(Long id, TaskStatus newStatus);

    void deleteTask(Long id);

    long countTotalTasks();
}
