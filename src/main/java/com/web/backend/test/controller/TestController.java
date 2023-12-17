package com.web.backend.test.controller;

import com.web.backend.test.domain.Language;
import com.web.backend.test.service.TestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
