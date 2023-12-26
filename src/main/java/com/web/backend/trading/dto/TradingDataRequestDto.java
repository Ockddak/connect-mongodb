package com.web.backend.trading.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(value = 6) @Max(value = 6)
    private String startDate;
    @NotBlank
    @Min(value = 6) @Max(value = 6)
    private String endDate;
    @NotBlank
    @Min(value = 5) @Max(value = 5)
    private String lawdCd; // "11350" 노원구

}
