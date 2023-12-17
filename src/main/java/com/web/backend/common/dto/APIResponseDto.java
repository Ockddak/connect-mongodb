package com.web.backend.common.dto;

import org.springframework.http.HttpStatus;

public class APIResponseDto {

    HttpStatus status;
    String message;
    String data;
    APIExceptionDto exception;

}
