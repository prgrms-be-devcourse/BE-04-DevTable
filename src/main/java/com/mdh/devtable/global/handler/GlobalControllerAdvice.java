package com.mdh.devtable.global.handler;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.global.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;


@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("MethodArgumentNotValidException");
        problemDetail.setProperty("validationError", e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::of)
                .toList());

        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(),
                problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("HttpMessageNotReadableException");

        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleMessageNotReadable(OptimisticLockingFailureException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("OptimisticLockingFailureException");

        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("RuntimeException");

        log.warn("런타임 예외가 발생했습니다. {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }
}