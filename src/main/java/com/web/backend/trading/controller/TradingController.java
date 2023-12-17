package com.web.backend.trading.controller;

import com.web.backend.trading.service.TradingService;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/trading")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TradingController {

    private final TradingService tradingService;


//    @GetMapping
//    public HttpResponse
}
