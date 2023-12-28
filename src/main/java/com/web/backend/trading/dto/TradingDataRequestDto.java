package com.web.backend.trading.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TradingDataRequestDto {

    @NotBlank
    @Length(min = 6, max = 6)
    private String startDate;
    @NotBlank
    @Length(min = 6, max = 6)
    private String endDate;
    @NotBlank
    @Length(min = 5, max = 5)
    private String lawdCd; // "11350" 노원구

}
