package com.kanban.config;

import com.kanban.domain.entity.Board;
import com.kanban.domain.entity.Task;
import com.kanban.domain.entity.TaskColumn;
import com.kanban.domain.enums.TaskPriority;
import com.kanban.domain.enums.TaskStatus;
import com.kanban.repository.BoardRepository;
import com.kanban.repository.TaskColumnRepository;
import com.kanban.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BoardRepository boardRepository;
    private final TaskColumnRepository columnRepository;
    private final TaskRepository taskRepository;

    public DataInitializer(BoardRepository boardRepository,
                           TaskColumnRepository columnRepository,
                           TaskRepository taskRepository) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (boardRepository.count() > 0) {
            return;
        }

        // 1. Create Default Board for Olhar Territorial
        Board board = new Board("Olhar Territorial — Quadro de Tarefas",
                "Quadro oficial sincronizado com as tasks reais de .agents/tasks do projeto Olhar Territorial");
        board = boardRepository.save(board);

        // 2. Create GitHub Projects style columns
        TaskColumn colBacklog = new TaskColumn("Backlog", TaskStatus.BACKLOG, 0, "#6e7681");
        colBacklog.setBoard(board);
        colBacklog = columnRepository.save(colBacklog);

        TaskColumn colTodo = new TaskColumn("Todo", TaskStatus.TODO, 1, "#58a6ff");
        colTodo.setBoard(board);
        colTodo = columnRepository.save(colTodo);

        TaskColumn colInProgress = new TaskColumn("In Progress", TaskStatus.IN_PROGRESS, 2, "#d29922");
        colInProgress.setBoard(board);
        colInProgress = columnRepository.save(colInProgress);

        TaskColumn colReview = new TaskColumn("In Review", TaskStatus.IN_REVIEW, 3, "#bc8cff");
        colReview.setBoard(board);
        colReview = columnRepository.save(colReview);

        TaskColumn colDone = new TaskColumn("Done", TaskStatus.DONE, 4, "#3fb950");
        colDone.setBoard(board);
        colDone = columnRepository.save(colDone);

        // 3. Try to dynamically load all tasks with FULL markdown descriptions from the project tasks folder
        File tasksDir = new File("C:/Users/tizo/Documents/olhar-territorial/.agents/tasks");
        if (tasksDir.exists() && tasksDir.isDirectory()) {
            loadTasksFromDirectory(tasksDir, Map.of(
                    "backlog", colBacklog,
                    "active", colInProgress, // will be distributed to in_progress / in_review
                    "completed", colDone
            ), colReview);
        }
    }

    private void loadTasksFromDirectory(File tasksDir, Map<String, TaskColumn> colMap, TaskColumn colReview) {
        List<File> files = new ArrayList<>();
        collectTaskFiles(tasksDir, files);

        // Sort so TASK-001 comes before TASK-002, etc.
        files.sort(Comparator.comparing(File::getName));

        int posBacklog = 0, posInProgress = 0, posReview = 0, posDone = 0;

        for (File file : files) {
            try {
                String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);

                String title = file.getName().replace(".md", "");
                String taskId = file.getName().substring(0, Math.min(8, file.getName().length()));
                String owner = "AI Assistant";
                String type = "feature";

                // Parse YAML frontmatter
                Matcher fmM = Pattern.compile("^---\\r?\\n([\\s\\S]*?)\\r?\\n---").matcher(content);
                if (fmM.find()) {
                    String fm = fmM.group(1);
                    Matcher tm = Pattern.compile("title:\\s*(.+)").matcher(fm);
                    if (tm.find()) title = tm.group(1).trim();

                    Matcher idM = Pattern.compile("id:\\s*(.+)").matcher(fm);
                    if (idM.find()) taskId = idM.group(1).trim();

                    Matcher om = Pattern.compile("owner:\\s*(.+)").matcher(fm);
                    if (om.find()) owner = om.group(1).trim();

                    Matcher typM = Pattern.compile("type:\\s*(.+)").matcher(fm);
                    if (typM.find()) type = typM.group(1).trim();
                }

                // Extract entire markdown body as full description
                String fullDescription = content;
                int fmEnd = content.indexOf("---", 3);
                if (fmEnd != -1) {
                    fullDescription = content.substring(fmEnd + 3).trim();
                }

                String parentDir = file.getParentFile().getName();
                TaskColumn targetCol;
                TaskStatus status;
                TaskPriority priority;
                int position;

                if ("completed".equalsIgnoreCase(parentDir)) {
                    targetCol = colMap.get("completed");
                    status = TaskStatus.DONE;
                    priority = TaskPriority.HIGH;
                    position = posDone++;
                } else if ("active".equalsIgnoreCase(parentDir)) {
                    if (file.getName().contains("008") || file.getName().contains("009")) {
                        targetCol = colReview;
                        status = TaskStatus.IN_REVIEW;
                        priority = file.getName().contains("008") ? TaskPriority.HIGH : TaskPriority.MEDIUM;
                        position = posReview++;
                    } else {
                        targetCol = colMap.get("active");
                        status = TaskStatus.IN_PROGRESS;
                        priority = TaskPriority.URGENT;
                        position = posInProgress++;
                    }
                } else {
                    targetCol = colMap.get("backlog");
                    status = TaskStatus.BACKLOG;
                    priority = TaskPriority.MEDIUM;
                    position = posBacklog++;
                }

                Task task = new Task("[" + taskId + "] " + title, fullDescription, status, priority);
                task.setColumn(targetCol);
                task.setPosition(position);
                task.setAssignee(owner);
                task.setLabels(Set.of(type, "synced", parentDir));
                task.setDueDate(LocalDate.now().plusDays(7));
                taskRepository.save(task);

            } catch (Exception e) {
                System.err.println("Error reading task file: " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private void collectTaskFiles(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                collectTaskFiles(f, list);
            } else if (f.getName().startsWith("TASK-") && f.getName().endsWith(".md")) {
                list.add(f);
            }
        }
    }
}
