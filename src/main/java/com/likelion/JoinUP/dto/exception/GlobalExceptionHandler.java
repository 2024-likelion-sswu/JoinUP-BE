package com.likelion.JoinUP.dto.exception;

import com.likelion.JoinUP.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
//        return ResponseEntity.status(400).body(new ApiResponse(false, ex.getMessage(), null));
//    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleGenericException(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(new ApiResponse(false, "Internal server error", null));
    }
}

