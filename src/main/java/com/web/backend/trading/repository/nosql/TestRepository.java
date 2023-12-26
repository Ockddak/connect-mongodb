package com.web.backend.trading.repository.nosql;

import com.web.backend.trading.domain.nosql.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends MongoRepository<Language, String> {

//    List<Language> findByNameRegex(String name);
}
