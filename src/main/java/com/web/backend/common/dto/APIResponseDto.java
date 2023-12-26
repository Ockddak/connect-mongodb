package com.web.backend.common.dto;

import org.springframework.http.HttpStatus;

public class APIResponseDto<T> {

    private HttpStatus status;
    private String message;
    private T data;
    private APIExceptionDto exception;

}
