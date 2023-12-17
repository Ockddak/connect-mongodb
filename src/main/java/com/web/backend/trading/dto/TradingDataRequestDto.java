package com.web.backend.trading.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradingDataRequestDto {

    @NotBlank
    private String startDate;
    @NotBlank
    private String endDate;
    @NotBlank
    private String lawdCd; // "11350" 노원구

}
