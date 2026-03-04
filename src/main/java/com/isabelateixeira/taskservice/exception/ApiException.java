package com.isabelateixeira.taskservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

    @Getter
    public class ApiException extends RuntimeException {

        private final HttpStatus status;

        public ApiException(String message, HttpStatus status) {
            super(message);
            this.status = status;
        }

        public static ApiException taskNotFound() {
            return new ApiException("Task not found", HttpStatus.NOT_FOUND);
        }

    }