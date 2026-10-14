package com.kanban.repository;

import com.kanban.domain.entity.TaskColumn;
import com.kanban.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {

    List<TaskColumn> findByBoardIdOrderByPositionAsc(Long boardId);

    Optional<TaskColumn> findByStatusKey(TaskStatus statusKey);

    Optional<TaskColumn> findFirstByBoardIdAndStatusKey(Long boardId, TaskStatus statusKey);
}
