package com.web.backend.test.repository;

import com.web.backend.test.domain.Language;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
class TestRepositoryTest {

    @Autowired
    TestRepository testRepository;

    @Test
    void findAll() {
        List<Language> all = testRepository.findAll();
        for (Language language : all) {
            System.out.println(language.toString());
//            System.out.println("language.getId() = " + language.getTitle());
        }
    }

}