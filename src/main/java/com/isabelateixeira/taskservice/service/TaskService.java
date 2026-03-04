package com.isabelateixeira.taskservice.service;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.domain.enums.TaskPriority;
import com.isabelateixeira.taskservice.domain.enums.TaskStatus;
import com.isabelateixeira.taskservice.exception.ApiException;
import com.isabelateixeira.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().length() < 3 || task.getTitle().length() > 100) {
            throw new ApiException("Title must be between 3 and 100 characters", HttpStatus.BAD_REQUEST);
        }

        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new ApiException("The due date cannot be in the past", HttpStatus.BAD_REQUEST);
        }
        return taskRepository.save(task);
    }

    public List<Task> getTasks(String statusParam, String priorityParam) {
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
        return findTaskById(id);
    }

    public Task updateTask(String id, Task taskUpdate) {
        Task task = findTaskById(id);

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new ApiException("Completed tasks cannot be edited",HttpStatus.BAD_REQUEST);
        }

        Optional.ofNullable(taskUpdate.getTitle()).ifPresent(task::setTitle);
        Optional.ofNullable(taskUpdate.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(taskUpdate.getStatus()).ifPresent(task::setStatus);
        Optional.ofNullable(taskUpdate.getPriority()).ifPresent(task::setPriority);
        Optional.ofNullable(taskUpdate.getDueDate()).ifPresent(task::setDueDate);

        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task findTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found", HttpStatus.NOT_FOUND));
    }
}
