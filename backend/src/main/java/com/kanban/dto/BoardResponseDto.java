package com.kanban.dto;

import com.kanban.domain.entity.Board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardResponseDto {

    private Long id;
    private String name;
    private String description;
    private int totalTasks;
    private List<ColumnResponseDto> columns = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BoardResponseDto() {
    }

    public static BoardResponseDto fromEntity(Board board) {
        if (board == null) return null;
        BoardResponseDto dto = new BoardResponseDto();
        dto.setId(board.getId());
        dto.setName(board.getName());
        dto.setDescription(board.getDescription());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setUpdatedAt(board.getUpdatedAt());

        if (board.getColumns() != null) {
            List<ColumnResponseDto> columnDtos = board.getColumns().stream()
                    .map(ColumnResponseDto::fromEntity)
                    .collect(Collectors.toList());
            dto.setColumns(columnDtos);
            dto.setTotalTasks(columnDtos.stream().mapToInt(ColumnResponseDto::getTaskCount).sum());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public List<ColumnResponseDto> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnResponseDto> columns) {
        this.columns = columns;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
