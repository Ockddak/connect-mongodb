package com.web.backend.trading.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoAddrResponseDto {

    private String fullAddress;
    private String bCode;
    private String pointX;
    private String pointY;

}
