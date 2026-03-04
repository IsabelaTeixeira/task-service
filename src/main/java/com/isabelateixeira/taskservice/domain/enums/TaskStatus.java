package com.isabelateixeira.taskservice.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.isabelateixeira.taskservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static TaskStatus from(String value) {
        if (value == null || value.isBlank()) {
            return TaskStatus.PENDING;
        }
        try {
            return TaskStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    "Invalid status. Allowed values: pending, in_progress, completed, cancelled",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
