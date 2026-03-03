package com.isabelateixeira.taskservice.service;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(String id, Task taskUpdate) {
        Optional<Task> existing = taskRepository.findById(id);
        if (existing.isPresent()) {
            Task task = existing.get();
            task.setTitle(taskUpdate.getTitle());
            task.setDescription(taskUpdate.getDescription());
            task.setStatus(taskUpdate.getStatus());
            task.setPriority(taskUpdate.getPriority());
            task.setDueDate(taskUpdate.getDueDate());
            return taskRepository.save(task);
        }
        return null;
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

}
