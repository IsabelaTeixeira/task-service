package com.isabelateixeira.taskservice.service;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.domain.enums.TaskPriority;
import com.isabelateixeira.taskservice.domain.enums.TaskStatus;
import com.isabelateixeira.taskservice.exception.ApiException;
import com.isabelateixeira.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task) {

        log.info("Creating task: title='{}'", task.getTitle());
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
            log.info("Defaulting status to PENDING");
        }

        if (task.getTitle() == null || task.getTitle().length() < 3 || task.getTitle().length() > 100) {
            log.warn("Invalid title: '{}'", task.getTitle());
            throw new ApiException("Title must be between 3 and 100 characters", HttpStatus.BAD_REQUEST);
        }

        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            log.warn("Due date is in the past: {}", task.getDueDate());
            throw new ApiException("The due date cannot be in the past", HttpStatus.BAD_REQUEST);
        }
        Task saved = taskRepository.save(task);
        log.info("Task created successfully: id={}", saved.getId());
        return saved;
    }

    public List<Task> getTasks(String statusParam, String priorityParam) {

        log.info("Fetching tasks with filters: status='{}', priority='{}'", statusParam, priorityParam);
        TaskStatus status = statusParam != null ? TaskStatus.from(statusParam) : null;
        TaskPriority priority = priorityParam != null ? TaskPriority.from(priorityParam) : null;

        if (status != null && priority != null)
            return taskRepository.findByStatusAndPriority(status, priority);

        if (status != null)
            return taskRepository.findByStatus(status);

        if (priority != null)
            return taskRepository.findByPriority(priority);

        return taskRepository.findAll();
    }

    public Task getTaskById(String id) {
        log.info("Fetching task by id={}", id);
        Task task = findTaskById(id);
        log.info("Task found: id={}, title='{}'", task.getId(), task.getTitle());
        return task;
    }

    public Task updateTask(String id, Task taskUpdate) {
        log.info("Updating task id={}", id);
        Task task = findTaskById(id);

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new ApiException("Completed tasks cannot be edited", HttpStatus.BAD_REQUEST);
        }

        Optional.ofNullable(taskUpdate.getTitle()).ifPresent(task::setTitle);
        Optional.ofNullable(taskUpdate.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(taskUpdate.getStatus()).ifPresent(task::setStatus);
        Optional.ofNullable(taskUpdate.getPriority()).ifPresent(task::setPriority);
        Optional.ofNullable(taskUpdate.getDueDate()).ifPresent(task::setDueDate);

        Task saved = taskRepository.save(task);
        log.info("Task updated successfully: id={}", saved.getId());
        return saved;
    }

    public void deleteTask(String id) {
        log.info("Deleting task id={}", id);
        Task task = findTaskById(id);
        taskRepository.delete(task);
        log.info("Task deleted successfully: id={}", id);
    }

    private Task findTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found: id={}", id);
                    return new ApiException("Task not found", HttpStatus.NOT_FOUND);
                });
    }
}
