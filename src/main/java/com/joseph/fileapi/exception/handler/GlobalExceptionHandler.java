package com.joseph.fileapi.exception.handler;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joseph.fileapi.exception.exceptions.ClientNotFoundException;
import com.joseph.fileapi.exception.exceptions.IllegalOperationException;
import com.joseph.fileapi.exception.exceptions.InvalidTimestampException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    private record ApiError(int status, String error, String message, String path, OffsetDateTime timestamp) {}

    @ExceptionHandler (IllegalOperationException.class)
    public ResponseEntity<ApiError> handleIllegalOperationException(IllegalOperationException e, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.FORBIDDEN.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI(), OffsetDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler (ClientNotFoundException.class)
    public ResponseEntity<ApiError> handleClientNotFoundException(ClientNotFoundException e, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI(), OffsetDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (InvalidTimestampException.class)
    public ResponseEntity<ApiError> handleInvalidTimestampException(InvalidTimestampException e, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.UNPROCESSABLE_CONTENT.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI(), OffsetDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
