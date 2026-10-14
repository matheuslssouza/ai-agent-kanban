package com.kanban.service.impl;

import com.kanban.domain.enums.TaskPriority;
import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.*;
import com.kanban.service.BoardService;
import com.kanban.service.AiIntegrationService;
import com.kanban.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiIntegrationServiceImpl implements AiIntegrationService {

    private final TaskService taskService;
    private final BoardService boardService;

    public AiIntegrationServiceImpl(TaskService taskService, BoardService boardService) {
        this.taskService = taskService;
        this.boardService = boardService;
    }

    @Override
    public AiCommandResponseDto executeCommand(AiCommandRequestDto request) {
        String action = request.getAction() != null ? request.getAction().toLowerCase().trim() : "natural_language";

        switch (action) {
            case "create_task":
                return handleCreateTask(request);

            case "mark_done":
            case "mark_task_done":
                return handleMarkDone(request);

            case "move_task":
                return handleMoveTask(request);

            case "update_task":
                return handleUpdateTask(request);

            case "delete_task":
                return handleDeleteTask(request);

            case "list_tasks":
                return handleListTasks(request);

            case "get_board_state":
                return AiCommandResponseDto.ok("Current board state retrieved", "get_board_state", boardService.getDefaultBoard());

            case "natural_language":
            default:
                return parseAndExecuteNaturalLanguage(request.getPrompt());
        }
    }

    private AiCommandResponseDto handleCreateTask(AiCommandRequestDto req) {
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            return AiCommandResponseDto.fail("Title is required to create a task", "create_task");
        }
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle(req.getTitle().trim());
        dto.setDescription(req.getDescription() != null ? req.getDescription() : "");
        dto.setStatus(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO);
        dto.setPriority(req.getPriority() != null ? req.getPriority() : TaskPriority.MEDIUM);
        dto.setAssignee(req.getAssignee() != null ? req.getAssignee() : "AI Assistant");
        dto.setColumnId(req.getColumnId());
        dto.setLabels(req.getLabels() != null ? req.getLabels() : Set.of("ai-agent"));

        TaskResponseDto created = taskService.createTask(dto);
        return AiCommandResponseDto.ok("Task created successfully #" + created.getId() + ": " + created.getTitle(), "create_task", created);
    }

    private AiCommandResponseDto handleMarkDone(AiCommandRequestDto req) {
        if (req.getTaskId() == null) {
            return AiCommandResponseDto.fail("Task ID is required to mark as done", "mark_done");
        }
        TaskResponseDto done = taskService.markAsDone(req.getTaskId());
        return AiCommandResponseDto.ok("Task #" + req.getTaskId() + " marked as DONE", "mark_done", done);
    }

    private AiCommandResponseDto handleMoveTask(AiCommandRequestDto req) {
        if (req.getTaskId() == null) {
            return AiCommandResponseDto.fail("Task ID is required to move task", "move_task");
        }
        TaskMoveDto moveDto = new TaskMoveDto();
        moveDto.setTargetColumnId(req.getColumnId());
        moveDto.setTargetStatus(req.getStatus());
        moveDto.setTargetPosition(req.getPosition() != null ? req.getPosition() : 0);

        TaskResponseDto moved = taskService.moveTask(req.getTaskId(), moveDto);
        return AiCommandResponseDto.ok("Task #" + req.getTaskId() + " moved to status: " + moved.getStatus(), "move_task", moved);
    }

    private AiCommandResponseDto handleUpdateTask(AiCommandRequestDto req) {
        if (req.getTaskId() == null) {
            return AiCommandResponseDto.fail("Task ID is required to update task", "update_task");
        }
        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle(req.getTitle());
        updateDto.setDescription(req.getDescription());
        updateDto.setPriority(req.getPriority());
        updateDto.setStatus(req.getStatus());
        updateDto.setLabels(req.getLabels());
        updateDto.setAssignee(req.getAssignee());

        TaskResponseDto updated = taskService.updateTask(req.getTaskId(), updateDto);
        return AiCommandResponseDto.ok("Task #" + req.getTaskId() + " updated", "update_task", updated);
    }

    private AiCommandResponseDto handleDeleteTask(AiCommandRequestDto req) {
        if (req.getTaskId() == null) {
            return AiCommandResponseDto.fail("Task ID is required to delete task", "delete_task");
        }
        taskService.deleteTask(req.getTaskId());
        return AiCommandResponseDto.ok("Task #" + req.getTaskId() + " deleted", "delete_task", Map.of("deletedId", req.getTaskId()));
    }

    private AiCommandResponseDto handleListTasks(AiCommandRequestDto req) {
        List<TaskResponseDto> tasks;
        if (req.getStatus() != null) {
            tasks = taskService.getTasksByStatus(req.getStatus());
        } else if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            tasks = taskService.searchTasks(req.getTitle());
        } else {
            tasks = taskService.getAllTasks();
        }
        return AiCommandResponseDto.ok("Found " + tasks.size() + " tasks", "list_tasks", tasks);
    }

    private AiCommandResponseDto parseAndExecuteNaturalLanguage(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return AiCommandResponseDto.fail("Empty prompt provided", "natural_language");
        }

        String lower = prompt.toLowerCase().trim();

        // Match mark done: "marcar como feita 3", "marcar 3 como feita", "mark #3 as done", "concluir tarefa 3"
        Pattern donePattern = Pattern.compile("(?:concluir|finalizar|feita|pronto|marcar.*?como feita|mark.*?done)\\D*?(\\d+)|(?:marcar|mark)\\D*?(\\d+)\\D*?(?:como feita|as done)", Pattern.CASE_INSENSITIVE);
        Matcher doneMatcher = donePattern.matcher(lower);
        if (doneMatcher.find()) {
            String idStr = doneMatcher.group(1) != null ? doneMatcher.group(1) : doneMatcher.group(2);
            Long id = Long.parseLong(idStr);
            TaskResponseDto task = taskService.markAsDone(id);
            return AiCommandResponseDto.ok("Tarefa #" + id + " marcada como CONCLUÍDA / DONE com sucesso!", "mark_done", task);
        }

        // Match move: "mover 3 para in progress", "move 3 to in_progress", "mover tarefa 2 para concluida"
        Pattern movePattern = Pattern.compile("(?:mover|move)\\D*?(\\d+)\\D*?(?:para|to)\\D*?([a-zA-Z_ -]+)", Pattern.CASE_INSENSITIVE);
        Matcher moveMatcher = movePattern.matcher(lower);
        if (moveMatcher.find()) {
            Long id = Long.parseLong(moveMatcher.group(1));
            String colStr = moveMatcher.group(2).trim();
            TaskStatus targetStatus = TaskStatus.TODO;
            if (colStr.contains("progress") || colStr.contains("progresso") || colStr.contains("andamento")) {
                targetStatus = TaskStatus.IN_PROGRESS;
            } else if (colStr.contains("done") || colStr.contains("conclu") || colStr.contains("feita")) {
                targetStatus = TaskStatus.DONE;
            } else if (colStr.contains("review") || colStr.contains("revis")) {
                targetStatus = TaskStatus.IN_REVIEW;
            } else if (colStr.contains("backlog")) {
                targetStatus = TaskStatus.BACKLOG;
            }
            TaskResponseDto moved = taskService.updateTaskStatus(id, targetStatus);
            return AiCommandResponseDto.ok("Tarefa #" + id + " movida para " + targetStatus, "move_task", moved);
        }

        // Match delete: "deletar tarefa 3", "delete 3", "remover card 4"
        Pattern deletePattern = Pattern.compile("(?:deletar|remover|excluir|delete|remove)\\D*?(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher deleteMatcher = deletePattern.matcher(lower);
        if (deleteMatcher.find()) {
            Long id = Long.parseLong(deleteMatcher.group(1));
            taskService.deleteTask(id);
            return AiCommandResponseDto.ok("Tarefa #" + id + " removida com sucesso", "delete_task", Map.of("deletedId", id));
        }

        // Match create: "criar tarefa: ...", "create task: ...", "adicionar item: ..."
        Pattern createPattern = Pattern.compile("(?:criar tarefa|criar task|adicionar tarefa|nova tarefa|create task|add task)[:\\s]+(.+)", Pattern.CASE_INSENSITIVE);
        Matcher createMatcher = createPattern.matcher(prompt);
        if (createMatcher.find()) {
            String content = createMatcher.group(1).trim();
            TaskPriority priority = TaskPriority.MEDIUM;
            if (content.toLowerCase().contains("urgente") || content.toLowerCase().contains("urgent")) {
                priority = TaskPriority.URGENT;
            } else if (content.toLowerCase().contains("alta") || content.toLowerCase().contains("high")) {
                priority = TaskPriority.HIGH;
            } else if (content.toLowerCase().contains("baixa") || content.toLowerCase().contains("low")) {
                priority = TaskPriority.LOW;
            }

            TaskCreateDto createDto = new TaskCreateDto();
            createDto.setTitle(content.replaceAll("(?i)\\b(com prioridade (alta|baixa|media|urgente)|with (high|low|medium|urgent) priority)\\b", "").trim());
            createDto.setDescription("Criado automaticamente via IA Local: " + prompt);
            createDto.setPriority(priority);
            createDto.setStatus(TaskStatus.TODO);
            createDto.setLabels(Set.of("ai-generated", "feature"));
            createDto.setAssignee("AI Assistant");

            TaskResponseDto created = taskService.createTask(createDto);
            return AiCommandResponseDto.ok("Tarefa #" + created.getId() + " criada com sucesso: \"" + created.getTitle() + "\" [Prioridade: " + priority + "]", "create_task", created);
        }

        // Match list: "listar tarefas", "mostrar kanban", "list tasks"
        if (lower.contains("listar") || lower.contains("mostrar") || lower.contains("list") || lower.contains("show")) {
            List<TaskResponseDto> all = taskService.getAllTasks();
            return AiCommandResponseDto.ok("Listando todas as " + all.size() + " tarefas do quadro", "list_tasks", all);
        }

        // Fallback: If prompt didn't match specific command, treat it as a quick task to create!
        TaskCreateDto fallback = new TaskCreateDto();
        fallback.setTitle(prompt.length() > 60 ? prompt.substring(0, 57) + "..." : prompt);
        fallback.setDescription(prompt);
        fallback.setPriority(TaskPriority.MEDIUM);
        fallback.setStatus(TaskStatus.TODO);
        fallback.setLabels(Set.of("ai-prompt"));
        fallback.setAssignee("AI Assistant");
        TaskResponseDto created = taskService.createTask(fallback);
        return AiCommandResponseDto.ok("Criada nova tarefa #" + created.getId() + " a partir do seu prompt: \"" + created.getTitle() + "\"", "create_task", created);
    }

    @Override
    public Map<String, Object> getMcpManifest() {
        Map<String, Object> manifest = new LinkedHashMap<>();
        manifest.put("name", "kanban-projects-mcp");
        manifest.put("version", "1.0.0");
        manifest.put("description", "Local Model Context Protocol (MCP) server for GitHub Projects-style Kanban Board");
        manifest.put("tools", getMcpTools());
        return manifest;
    }

    @Override
    public List<Map<String, Object>> getMcpTools() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // tool: create_task
        tools.add(Map.of(
                "name", "create_task",
                "description", "Create a new task in the Kanban board (similar to a GitHub Project card)",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "title", Map.of("type", "string", "description", "Title of the task"),
                                "description", Map.of("type", "string", "description", "Detailed markdown description"),
                                "status", Map.of("type", "string", "enum", List.of("BACKLOG", "TODO", "IN_PROGRESS", "IN_REVIEW", "DONE")),
                                "priority", Map.of("type", "string", "enum", List.of("LOW", "MEDIUM", "HIGH", "URGENT")),
                                "assignee", Map.of("type", "string", "description", "Assignee name or agent"),
                                "labels", Map.of("type", "array", "items", Map.of("type", "string"), "description", "Labels/tags")
                        ),
                        "required", List.of("title")
                )
        ));

        // tool: mark_task_done
        tools.add(Map.of(
                "name", "mark_task_done",
                "description", "Marks a specific task as DONE and moves it to the Done column",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "taskId", Map.of("type", "integer", "description", "The ID of the task")
                        ),
                        "required", List.of("taskId")
                )
        ));

        // tool: move_task
        tools.add(Map.of(
                "name", "move_task",
                "description", "Move a task card to another column or position in the Kanban board",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "taskId", Map.of("type", "integer", "description", "ID of the task to move"),
                                "targetStatus", Map.of("type", "string", "enum", List.of("BACKLOG", "TODO", "IN_PROGRESS", "IN_REVIEW", "DONE")),
                                "targetPosition", Map.of("type", "integer", "description", "Position index in column")
                        ),
                        "required", List.of("taskId", "targetStatus")
                )
        ));

        // tool: list_tasks
        tools.add(Map.of(
                "name", "list_tasks",
                "description", "List all tasks from the Kanban board, optionally filtering by status or search query",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "status", Map.of("type", "string", "enum", List.of("BACKLOG", "TODO", "IN_PROGRESS", "IN_REVIEW", "DONE")),
                                "query", Map.of("type", "string", "description", "Search keywords in title or description")
                        )
                )
        ));

        // tool: get_board_state
        tools.add(Map.of(
                "name", "get_board_state",
                "description", "Retrieve full board structure including all columns, cards, and counts",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Collections.emptyMap()
                )
        ));

        // tool: delete_task
        tools.add(Map.of(
                "name", "delete_task",
                "description", "Delete a task from the board by its ID",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "taskId", Map.of("type", "integer", "description", "ID of the task to delete")
                        ),
                        "required", List.of("taskId")
                )
        ));

        return tools;
    }
}
