package com.isabelateixeira.taskservice.repository;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.domain.enums.TaskPriority;
import com.isabelateixeira.taskservice.domain.enums.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task,String> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);
}
