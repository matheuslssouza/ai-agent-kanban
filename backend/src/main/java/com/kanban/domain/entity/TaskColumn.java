package com.kanban.domain.entity;

import com.kanban.domain.enums.TaskStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "columns")
public class TaskColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus statusKey;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(length = 20)
    private String color = "#6e7681";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Task> tasks = new ArrayList<>();

    public TaskColumn() {
    }

    public TaskColumn(String name, TaskStatus statusKey, Integer position, String color) {
        this.name = name;
        this.statusKey = statusKey;
        this.position = position;
        this.color = color;
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setColumn(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setColumn(null);
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

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
