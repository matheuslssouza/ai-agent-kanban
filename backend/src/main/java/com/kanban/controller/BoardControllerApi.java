package com.kanban.controller;

import com.kanban.dto.BoardResponseDto;
import com.kanban.dto.ColumnResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Boards", description = "Endpoints for Board and Column structures")
public interface BoardControllerApi {

    @Operation(summary = "Get the default Kanban board with all columns and tasks")
    ResponseEntity<BoardResponseDto> getDefaultBoard();

    @Operation(summary = "Get all boards")
    ResponseEntity<List<BoardResponseDto>> getAllBoards();

    @Operation(summary = "Get board by ID")
    ResponseEntity<BoardResponseDto> getBoardById(Long id);

    @Operation(summary = "Get columns for a specific board")
    ResponseEntity<List<ColumnResponseDto>> getColumnsByBoard(Long boardId);
}
