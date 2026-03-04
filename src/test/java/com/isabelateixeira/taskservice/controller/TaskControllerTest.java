package com.isabelateixeira.taskservice.controller;

import com.isabelateixeira.taskservice.domain.Task;
import com.isabelateixeira.taskservice.domain.enums.TaskPriority;
import com.isabelateixeira.taskservice.domain.enums.TaskStatus;
import com.isabelateixeira.taskservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Exemplo de task para os testes
    private Task sampleTask() {
        return Task.builder()
                .id("1")
                .title("Comprar leite")
                .description("Descrição")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("GET /tasks sem parâmetros deve retornar todas as tasks")
    void testGetTasksWithoutParams() throws Exception {
        Task task = sampleTask();
        Mockito.when(taskService.getTasks(null, null)).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk()) // verifica status 200
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Comprar leite"));
    }

    @Test
    @DisplayName("GET /tasks com parâmetros deve retornar tasks filtradas")
    void testGetTasksWithParams() throws Exception {
        Task task = sampleTask();
        Mockito.when(taskService.getTasks("IN_PROGRESS", "HIGH")).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks")
                        .param("status", "IN_PROGRESS")
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("in_progress"))
                .andExpect(jsonPath("$[0].priority").value("high"));
    }

    @Test
    @DisplayName("GET /tasks/{id} deve retornar a task pelo ID")
    void testGetTaskById() throws Exception {
        Task task = sampleTask();
        Mockito.when(taskService.getTaskById("1")).thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Comprar leite"));
    }

    @Test
    @DisplayName("POST /tasks deve criar uma nova task")
    void testCreateTask() throws Exception {
        Task task = sampleTask();
        Mockito.when(taskService.createTask(Mockito.any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Comprar leite",
                                    "description": "Descrição",
                                    "status": "IN_PROGRESS",
                                    "priority": "HIGH",
                                    "dueDate": "2026-03-05"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Comprar leite"))
                .andExpect(jsonPath("$.status").value("in_progress"));
    }

    @Test
    @DisplayName("PUT /tasks/{id} deve atualizar a task existente")
    void testUpdateTask() throws Exception {
        Task updatedTask = Task.builder()
                .id("1")
                .title("Comprar pão")
                .description("Descrição atualizada")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now())
                .build();

        Mockito.when(taskService.updateTask(Mockito.eq("1"), Mockito.any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Comprar pão",
                                    "description": "Descrição atualizada",
                                    "priority": "MEDIUM"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Comprar pão"))
                .andExpect(jsonPath("$.priority").value("medium"));
    }

    @Test
    @DisplayName("DELETE /tasks/{id} deve remover a task")
    void testDeleteTask() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask("1");

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }
}