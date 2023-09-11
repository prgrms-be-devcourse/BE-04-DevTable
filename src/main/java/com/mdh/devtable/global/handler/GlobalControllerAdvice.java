package com.mdh.devtable.global.handler;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.global.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.NoSuchElementException;


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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("MethodArgumentTypeMismatchException");

        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleNoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("NoSuchElementException");

        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }
}