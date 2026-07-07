package com.neuroforge_nexus.repository;

import com.neuroforge_nexus.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySprintId(Long sprintId);
    long countByStatusAndSprintId(String status, Long sprintId);
}
