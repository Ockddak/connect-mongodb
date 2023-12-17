package com.web.backend.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class APIExceptionDto {

    private String errorCode;
    private String errorMessage;

    @Builder
    public APIExceptionDto(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
