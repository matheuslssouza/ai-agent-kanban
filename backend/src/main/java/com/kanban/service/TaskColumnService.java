package com.kanban.service;

import com.kanban.domain.enums.TaskStatus;
import com.kanban.dto.ColumnResponseDto;

import java.util.List;

public interface TaskColumnService {

    List<ColumnResponseDto> getColumnsByBoard(Long boardId);

    ColumnResponseDto getColumnById(Long id);

    ColumnResponseDto getColumnByStatus(TaskStatus statusKey);
}
