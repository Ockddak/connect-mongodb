package com.web.backend.test.service;

import com.web.backend.test.domain.Language;
import com.web.backend.test.repository.TestRepository;
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
