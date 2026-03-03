package com.isabelateixeira.taskservice.repository;

import com.isabelateixeira.taskservice.domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task,String> {
}
