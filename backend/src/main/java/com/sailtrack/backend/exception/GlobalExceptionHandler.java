package com.sailtrack.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice   // 拦截所有 Controller
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class) // 捕获业务异常
    public ResponseEntity<Map<String, Object>> handleBusiness(RuntimeException ex) {
        ex.printStackTrace();
        String message = ex.getMessage() != null ? ex.getMessage() : "未知错误";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(Map.of("ok", false, "code",HttpStatus.BAD_REQUEST.value(),"message", message));
    }
}