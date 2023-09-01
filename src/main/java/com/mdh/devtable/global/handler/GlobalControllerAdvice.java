package com.mdh.devtable.global.handler;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.global.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<ProblemDetail> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle("MethodArgumentNotValidException");
        problemDetail.setProperty("validationError", e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::of)
                .toList());

        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), problemDetail);
    }
}