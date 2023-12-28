package com.web.backend.trading.controller;

import com.web.backend.trading.domain.rdb.TradingData;
import com.web.backend.trading.domain.rdb.TradingHistory;
import com.web.backend.trading.dto.TradingDataRequestDto;
import com.web.backend.trading.service.TradingService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/public/data/trading")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TradingController {

    private final TradingService tradingService;

    @PostMapping
    public ResponseEntity getPublicData(
            @RequestBody @Validated TradingDataRequestDto tradingDataRequestDto,
            HttpServletRequest request) {

        log.info("{} START", request.getRequestURL());
        log.info("request Param: {}", tradingDataRequestDto.toString());
        List<String> tradingData = tradingService.getPublicData(tradingDataRequestDto);

        if (tradingData.isEmpty()) {
            return new ResponseEntity("Invalid Your Input Data", HttpStatus.BAD_REQUEST);
        }

        log.info("{} END", request.getRequestURL());

        return new ResponseEntity(tradingData, HttpStatus.OK);
    }

    @PostMapping("/kakao")
    public ResponseEntity getProcessingKakao(
            @RequestBody @Validated TradingDataRequestDto tradingDataRequestDto,
            HttpServletRequest request
    ) {
        List<TradingData> processingKakao = tradingService.getProcessingKakao(
                tradingDataRequestDto);
        if (processingKakao.isEmpty()) {
            return new ResponseEntity("Check your server log", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(processingKakao, HttpStatus.OK);
    }

}
