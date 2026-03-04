package com.isabelateixeira.taskservice.controller;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) String status, @RequestParam(required = false) String priority) {
        log.info("GET /tasks called with status='{}', priority='{}'", status, priority);
        List<Task> tasks = taskService.getTasks(status, priority);
        log.info("GET /tasks returned {} tasks", tasks.size());
        return tasks;
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable String id) {
        log.info("GET /tasks/{} called", id);
        Task task = taskService.getTaskById(id);
        log.info("GET /tasks/{} returned task title='{}'", id, task.getTitle());
        return task;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        log.info("POST /tasks called with title='{}'", task.getTitle());
        Task created = taskService.createTask(task);
        log.info("POST /tasks created task id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Task updateTask(@Valid @PathVariable String id, @RequestBody Task task) {
        log.info("PUT /tasks/{} called", id);
        Task updated = taskService.updateTask(id, task);
        log.info("PUT /tasks/{} updated task title='{}'", id, updated.getTitle());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String id) {
        log.info("DELETE /tasks/{} called", id);
        taskService.deleteTask(id);
        log.info("DELETE /tasks/{} completed", id);
    }
}