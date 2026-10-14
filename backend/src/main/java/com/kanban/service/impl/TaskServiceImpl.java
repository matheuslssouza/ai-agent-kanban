package com.kanban.service.impl;

import com.kanban.domain.entity.Task;
import com.kanban.domain.entity.TaskColumn;
import com.kanban.domain.enums.TaskPriority;
import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.TaskCreateDto;
import com.kanban.dto.TaskMoveDto;
import com.kanban.dto.TaskResponseDto;
import com.kanban.dto.TaskUpdateDto;
import com.kanban.exception.ResourceNotFoundException;
import com.kanban.repository.BoardRepository;
import com.kanban.repository.TaskColumnRepository;
import com.kanban.repository.TaskRepository;
import com.kanban.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskColumnRepository columnRepository,
                           BoardRepository boardRepository) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return TaskResponseDto.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByColumn(Long columnId) {
        return taskRepository.findByColumnIdOrderByPositionAsc(columnId).stream()
                .map(TaskResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatusOrderByPositionAsc(status).stream()
                .map(TaskResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskRepository.searchTasks(query.trim()).stream()
                .map(TaskResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto createTask(TaskCreateDto createDto) {
        Task task = new Task();
        task.setTitle(createDto.getTitle());
        task.setDescription(createDto.getDescription() != null ? createDto.getDescription() : "");
        task.setStatus(createDto.getStatus() != null ? createDto.getStatus() : TaskStatus.TODO);
        task.setPriority(createDto.getPriority() != null ? createDto.getPriority() : TaskPriority.MEDIUM);
        task.setAssignee(createDto.getAssignee() != null ? createDto.getAssignee() : "AI Assistant");
        task.setDueDate(createDto.getDueDate());

        if (createDto.getLabels() != null) {
            task.setLabels(new HashSet<>(createDto.getLabels()));
        }

        // Determine column
        TaskColumn column = null;
        if (createDto.getColumnId() != null) {
            column = columnRepository.findById(createDto.getColumnId()).orElse(null);
        }
        if (column == null) {
            column = columnRepository.findByStatusKey(task.getStatus())
                    .orElseGet(() -> columnRepository.findAll().stream().findFirst().orElse(null));
        }

        if (column != null) {
            task.setColumn(column);
            task.setStatus(column.getStatusKey());
            int maxPos = taskRepository.findMaxPositionInColumn(column.getId());
            task.setPosition(maxPos + 1);
        } else {
            task.setPosition(0);
        }

        Task saved = taskRepository.save(task);
        return TaskResponseDto.fromEntity(saved);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskUpdateDto updateDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (updateDto.getTitle() != null && !updateDto.getTitle().trim().isEmpty()) {
            task.setTitle(updateDto.getTitle().trim());
        }
        if (updateDto.getDescription() != null) {
            task.setDescription(updateDto.getDescription());
        }
        if (updateDto.getPriority() != null) {
            task.setPriority(updateDto.getPriority());
        }
        if (updateDto.getLabels() != null) {
            task.setLabels(new HashSet<>(updateDto.getLabels()));
        }
        if (updateDto.getAssignee() != null) {
            task.setAssignee(updateDto.getAssignee());
        }
        if (updateDto.getDueDate() != null) {
            task.setDueDate(updateDto.getDueDate());
        }

        // If status changed
        if (updateDto.getStatus() != null && updateDto.getStatus() != task.getStatus()) {
            task.setStatus(updateDto.getStatus());
            TaskColumn targetCol = columnRepository.findByStatusKey(updateDto.getStatus()).orElse(null);
            if (targetCol != null && !targetCol.getId().equals(task.getColumn() != null ? task.getColumn().getId() : null)) {
                task.setColumn(targetCol);
                task.setPosition(taskRepository.findMaxPositionInColumn(targetCol.getId()) + 1);
            }
        }

        // If column changed explicitly
        if (updateDto.getColumnId() != null && (task.getColumn() == null || !updateDto.getColumnId().equals(task.getColumn().getId()))) {
            TaskColumn targetCol = columnRepository.findById(updateDto.getColumnId())
                    .orElseThrow(() -> new ResourceNotFoundException("Column not found with id: " + updateDto.getColumnId()));
            task.setColumn(targetCol);
            task.setStatus(targetCol.getStatusKey());
            task.setPosition(taskRepository.findMaxPositionInColumn(targetCol.getId()) + 1);
        }

        if (updateDto.getPosition() != null) {
            task.setPosition(updateDto.getPosition());
        }

        Task updated = taskRepository.save(task);
        return TaskResponseDto.fromEntity(updated);
    }

    @Override
    public TaskResponseDto moveTask(Long id, TaskMoveDto moveDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        TaskColumn sourceCol = task.getColumn();
        TaskColumn targetCol = null;

        if (moveDto.getTargetColumnId() != null) {
            targetCol = columnRepository.findById(moveDto.getTargetColumnId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target column not found with id: " + moveDto.getTargetColumnId()));
        } else if (moveDto.getTargetStatus() != null) {
            targetCol = columnRepository.findByStatusKey(moveDto.getTargetStatus())
                    .orElseThrow(() -> new ResourceNotFoundException("Column for status not found: " + moveDto.getTargetStatus()));
        } else {
            targetCol = sourceCol;
        }

        boolean isDifferentColumn = sourceCol == null || !sourceCol.getId().equals(targetCol.getId());

        if (isDifferentColumn) {
            // Reorder old column tasks
            if (sourceCol != null) {
                List<Task> oldColTasks = taskRepository.findByColumnIdOrderByPositionAsc(sourceCol.getId());
                int p = 0;
                for (Task t : oldColTasks) {
                    if (!t.getId().equals(task.getId())) {
                        t.setPosition(p++);
                        taskRepository.save(t);
                    }
                }
            }

            // Put into target column
            task.setColumn(targetCol);
            task.setStatus(targetCol.getStatusKey());

            List<Task> newColTasks = taskRepository.findByColumnIdOrderByPositionAsc(targetCol.getId());
            int targetPos = Math.min(Math.max(0, moveDto.getTargetPosition()), newColTasks.size());
            newColTasks.add(targetPos, task);

            for (int i = 0; i < newColTasks.size(); i++) {
                Task t = newColTasks.get(i);
                t.setPosition(i);
                taskRepository.save(t);
            }
        } else {
            // Reorder inside same column
            List<Task> sameColTasks = taskRepository.findByColumnIdOrderByPositionAsc(targetCol.getId());
            sameColTasks.removeIf(t -> t.getId().equals(task.getId()));
            int targetPos = Math.min(Math.max(0, moveDto.getTargetPosition()), sameColTasks.size());
            sameColTasks.add(targetPos, task);

            for (int i = 0; i < sameColTasks.size(); i++) {
                Task t = sameColTasks.get(i);
                t.setPosition(i);
                taskRepository.save(t);
            }
        }

        return TaskResponseDto.fromEntity(task);
    }

    @Override
    public TaskResponseDto markAsDone(Long id) {
        return updateTaskStatus(id, TaskStatus.DONE);
    }

    @Override
    public TaskResponseDto updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setStatus(newStatus);
        TaskColumn targetCol = columnRepository.findByStatusKey(newStatus).orElse(null);
        if (targetCol != null) {
            task.setColumn(targetCol);
            task.setPosition(taskRepository.findMaxPositionInColumn(targetCol.getId()) + 1);
        }

        Task saved = taskRepository.save(task);
        return TaskResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalTasks() {
        return taskRepository.count();
    }
}
