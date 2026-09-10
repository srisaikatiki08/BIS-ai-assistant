package com.bis.assistant.exception;

import com.bis.assistant.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ChatResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Validation error: ");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        }
        logger.warn("Validation failed: {}", sb);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ChatResponse.error(sb.toString().trim()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ChatResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ChatResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ChatResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled server exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ChatResponse.error("An internal error occurred while processing your request."));
    }
}
