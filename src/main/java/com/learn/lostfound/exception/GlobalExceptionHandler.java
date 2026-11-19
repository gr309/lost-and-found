package com.learn.lostfound.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleUploadLimit() {
        return ResponseEntity.status(413).body(Map.of("message", "Maximum image size is 5MB"));
    }

    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<?> handleStorageFailure(java.io.IOException ex) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("File operation failed", ex);
        return ResponseEntity.internalServerError().body(Map.of("message", "Unable to store image"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleException(
            ResponseStatusException ex
    ) {
        return ResponseEntity
            .status(ex.getStatusCode())
            .body(Map.of(
                "message",
                ex.getReason() != null
                    ? ex.getReason()
                    : "Request failed"
            ));
    }
}
