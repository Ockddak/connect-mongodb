package com.web.backend.trading.controller;

import com.web.backend.trading.domain.nosql.Language;
import com.web.backend.trading.service.TestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("api")
    public List<Language> test(
    ) {
        return testService.findAll();
    }

}
