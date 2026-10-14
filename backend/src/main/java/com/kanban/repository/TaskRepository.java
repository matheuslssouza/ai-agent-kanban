package com.kanban.repository;

import com.kanban.domain.entity.Task;
import com.kanban.domain.enums.TaskPriority;
import com.kanban.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByColumnIdOrderByPositionAsc(Long columnId);

    List<Task> findByStatusOrderByPositionAsc(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Task> searchTasks(@Param("query") String query);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);

    @Query("SELECT COALESCE(MAX(t.position), -1) FROM Task t WHERE t.column.id = :columnId")
    int findMaxPositionInColumn(@Param("columnId") Long columnId);
}
