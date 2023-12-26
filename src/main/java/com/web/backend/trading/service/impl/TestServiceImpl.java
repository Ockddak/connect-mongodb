package com.web.backend.trading.service.impl;

import com.web.backend.trading.domain.nosql.Language;
import com.web.backend.trading.repository.nosql.TestRepository;
import com.web.backend.trading.service.TestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository repository;

    @Override
    public List<Language> findAll() {
        return repository.findAll();
    }
}
