package com.mdh.user.global.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public class ProblemDetailUtil {

    public static ProblemDetail createProblemDetail(Exception e, HttpServletRequest request, HttpStatus status, String title) {
        var uri = request.getRequestURI();
        var problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setInstance(URI.create(uri));
        problemDetail.setTitle(title);
        return problemDetail;
    }
}