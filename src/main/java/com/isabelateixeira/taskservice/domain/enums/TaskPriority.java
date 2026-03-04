package com.isabelateixeira.taskservice.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.isabelateixeira.taskservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static TaskPriority from(String value) {
        try {
            return TaskPriority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    "Invalid priority. Allowed values: low, medium, high",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
