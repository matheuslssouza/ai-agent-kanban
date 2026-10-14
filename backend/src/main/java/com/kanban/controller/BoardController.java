package com.kanban.controller;

import com.kanban.dto.BoardResponseDto;
import com.kanban.dto.ColumnResponseDto;
import com.kanban.service.BoardService;
import com.kanban.service.TaskColumnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
public class BoardController implements BoardControllerApi {

    private final BoardService boardService;
    private final TaskColumnService columnService;

    public BoardController(BoardService boardService, TaskColumnService columnService) {
        this.boardService = boardService;
        this.columnService = columnService;
    }

    @Override
    @GetMapping("/default")
    public ResponseEntity<BoardResponseDto> getDefaultBoard() {
        return ResponseEntity.ok(boardService.getDefaultBoard());
    }

    @Override
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @Override
    @GetMapping("/{id}/columns")
    public ResponseEntity<List<ColumnResponseDto>> getColumnsByBoard(@PathVariable Long id) {
        return ResponseEntity.ok(columnService.getColumnsByBoard(id));
    }
}
