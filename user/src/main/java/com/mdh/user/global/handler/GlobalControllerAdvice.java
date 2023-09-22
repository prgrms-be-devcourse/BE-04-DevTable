package com.mdh.user.global.handler;

import com.mdh.user.global.ApiResponse;
import com.mdh.user.global.error.ValidationError;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.NoSuchElementException;


@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        var problemDetail = ProblemDetailUtil.createProblemDetail(e, request, HttpStatus.BAD_REQUEST, "MethodArgumentNotValidException");
        log.warn("잘못된 request body {}", e.getMessage(), e);
        problemDetail.setProperty("validationError", e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::of)
                .toList());
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        var problemDetail = ProblemDetailUtil.createProblemDetail(e, request, HttpStatus.BAD_REQUEST, "HttpMessageNotReadableException");
        log.warn("잘못된 request body(enum) {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleOptimisticLockingFailure(OptimisticLockingFailureException e, HttpServletRequest request) {
        var problemDetail = ProblemDetailUtil.createProblemDetail(e, request, HttpStatus.BAD_REQUEST, "OptimisticLockingFailureException");
        log.warn("동시 요청 오류 발생 {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        var problemDetail = ProblemDetailUtil.createProblemDetail(e, request, HttpStatus.BAD_REQUEST, "RuntimeException");
        log.warn("런타임 예외가 발생했습니다. {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        var problemDetail = ProblemDetailUtil.createProblemDetail(e, request, HttpStatus.BAD_REQUEST, "MethodArgumentTypeMismatchException");
        log.warn("잘못된 request body {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<ProblemDetail>> handleNoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        var problemDetail = ProblemDetailUtil.createProblemDetail(e, request, HttpStatus.BAD_REQUEST, "NoSuchElementException");
        log.warn("엔티티 객체 조회 실패 {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail), HttpStatus.BAD_REQUEST);
    }
}