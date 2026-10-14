package com.kanban.service.impl;

import com.kanban.domain.entity.TaskColumn;
import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.ColumnResponseDto;
import com.kanban.exception.ResourceNotFoundException;
import com.kanban.repository.TaskColumnRepository;
import com.kanban.service.TaskColumnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskColumnServiceImpl implements TaskColumnService {

    private final TaskColumnRepository columnRepository;

    public TaskColumnServiceImpl(TaskColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    @Override
    public List<ColumnResponseDto> getColumnsByBoard(Long boardId) {
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId).stream()
                .map(ColumnResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ColumnResponseDto getColumnById(Long id) {
        TaskColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found with id: " + id));
        return ColumnResponseDto.fromEntity(column);
    }

    @Override
    public ColumnResponseDto getColumnByStatus(TaskStatus statusKey) {
        TaskColumn column = columnRepository.findByStatusKey(statusKey)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found for status: " + statusKey));
        return ColumnResponseDto.fromEntity(column);
    }
}
