package com.isabelateixeira.taskservice.service;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.domain.enums.TaskPriority;
import com.isabelateixeira.taskservice.domain.enums.TaskStatus;
import com.isabelateixeira.taskservice.exception.ApiException;
import com.isabelateixeira.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Task sampleTask() {
        return Task.builder().id("1").title("Comprar leite").description("Descrição").status(TaskStatus.IN_PROGRESS).priority(TaskPriority.HIGH).dueDate(LocalDate.now().plusDays(1)) // data futura
                .build();
    }

    @Test
    @DisplayName("Criar task com dados válidos deve salvar com sucesso")
    void testCreateTaskSuccess() {
        Task task = sampleTask();

        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createTask(task);

        assertNotNull(result);
        assertEquals("Comprar leite", result.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Criar task com título nulo deve lançar ApiException")
    void testCreateTaskTitleNull() {
        Task task = sampleTask();
        task.setTitle(null);

        ApiException exception = assertThrows(ApiException.class, () -> taskService.createTask(task));
        assertEquals("Title must be between 3 and 100 characters", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar task com data passada deve lançar ApiException")
    void testCreateTaskDueDateInPast() {
        Task task = sampleTask();
        task.setDueDate(LocalDate.now().minusDays(1));

        ApiException exception = assertThrows(ApiException.class, () -> taskService.createTask(task));
        assertEquals("The due date cannot be in the past", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("getTasks sem parâmetros retorna todas as tasks")
    void testGetTasksNoParams() {
        Task task = sampleTask();
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.getTasks(null, null);

        assertEquals(1, result.size());
        assertEquals("Comprar leite", result.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getTasks com status retorna filtrado pelo status")
    void testGetTasksByStatus() {
        Task task = sampleTask();
        when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(List.of(task));

        List<Task> result = taskService.getTasks("IN_PROGRESS", null);

        assertEquals(1, result.size());
        assertEquals(TaskStatus.IN_PROGRESS, result.get(0).getStatus());
        verify(taskRepository, times(1)).findByStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("getTasks com priority retorna filtrado pela prioridade")
    void testGetTasksByPriority() {
        Task task = sampleTask();
        when(taskRepository.findByPriority(TaskPriority.HIGH)).thenReturn(List.of(task));

        List<Task> result = taskService.getTasks(null, "HIGH");

        assertEquals(1, result.size());
        assertEquals(TaskPriority.HIGH, result.get(0).getPriority());
        verify(taskRepository, times(1)).findByPriority(TaskPriority.HIGH);
    }

    @Test
    @DisplayName("getTasks com status e priority retorna filtrado por ambos")
    void testGetTasksByStatusAndPriority() {
        Task task = sampleTask();
        when(taskRepository.findByStatusAndPriority(TaskStatus.IN_PROGRESS, TaskPriority.HIGH)).thenReturn(List.of(task));

        List<Task> result = taskService.getTasks("IN_PROGRESS", "HIGH");

        assertEquals(1, result.size());
        assertEquals(TaskStatus.IN_PROGRESS, result.get(0).getStatus());
        assertEquals(TaskPriority.HIGH, result.get(0).getPriority());
        verify(taskRepository, times(1)).findByStatusAndPriority(TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
    }

    @Test
    @DisplayName("getTaskById retorna task existente")
    void testGetTaskById() {
        Task task = sampleTask();
        when(taskRepository.findById("1")).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById("1");

        assertNotNull(result);
        assertEquals("Comprar leite", result.getTitle());
    }

    @Test
    @DisplayName("getTaskById lança erro se task não existe")
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById("999")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> taskService.getTaskById("999"));
        assertEquals("Task not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("updateTask atualiza campos válidos")
    void testUpdateTaskSuccess() {
        Task task = sampleTask();
        Task updated = Task.builder().title("Comprar pão").description("Nova descrição").priority(TaskPriority.MEDIUM).build();

        when(taskRepository.findById("1")).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.updateTask("1", updated);

        assertEquals("Comprar pão", result.getTitle());
        assertEquals("Nova descrição", result.getDescription());
        assertEquals(TaskPriority.MEDIUM, result.getPriority());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("updateTask lança erro se task COMPLETED")
    void testUpdateTaskCompleted() {
        Task task = sampleTask();
        task.setStatus(TaskStatus.COMPLETED);

        Task updated = Task.builder().title("Comprar pão").build();

        when(taskRepository.findById("1")).thenReturn(Optional.of(task));

        ApiException exception = assertThrows(ApiException.class, () -> taskService.updateTask("1", updated));
        assertEquals("Completed tasks cannot be edited", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteTask remove task existente")
    void testDeleteTaskSuccess() {
        Task task = sampleTask();
        when(taskRepository.findById("1")).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask("1");

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    @DisplayName("deleteTask lança erro se task não existe")
    void testDeleteTaskNotFound() {
        when(taskRepository.findById("999")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> taskService.deleteTask("999"));
        assertEquals("Task not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(taskRepository, never()).delete(any());
    }
}


