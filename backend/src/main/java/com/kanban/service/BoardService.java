package com.kanban.service;

import com.kanban.dto.BoardResponseDto;

import java.util.List;

public interface BoardService {

    List<BoardResponseDto> getAllBoards();

    BoardResponseDto getBoardById(Long id);

    BoardResponseDto getDefaultBoard();
}
