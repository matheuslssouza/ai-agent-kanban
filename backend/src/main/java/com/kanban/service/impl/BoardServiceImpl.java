package com.kanban.service.impl;

import com.kanban.domain.entity.Board;
import com.kanban.dto.BoardResponseDto;
import com.kanban.exception.ResourceNotFoundException;
import com.kanban.repository.BoardRepository;
import com.kanban.service.BoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public List<BoardResponseDto> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(BoardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public BoardResponseDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + id));
        return BoardResponseDto.fromEntity(board);
    }

    @Override
    public BoardResponseDto getDefaultBoard() {
        Board board = boardRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("No default board found"));
        return BoardResponseDto.fromEntity(board);
    }
}
