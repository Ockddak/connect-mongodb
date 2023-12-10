package com.web.backend.test.repository;

import com.web.backend.test.domain.Language;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepository extends MongoRepository<Language, String> {

//    List<Language> findByNameRegex(String name);
}
