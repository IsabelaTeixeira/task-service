package com.isabelateixeira.taskservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isabelateixeira.taskservice.domain.enums.TaskPriority;
import com.isabelateixeira.taskservice.domain.enums.TaskStatus;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate dueDate;

    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
