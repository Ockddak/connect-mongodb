package com.web.backend.trading.repository.nosql;

import com.web.backend.trading.domain.nosql.TownHouse;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TownHouseMongoRepository extends MongoRepository<TownHouse, String> {

    List<TownHouse> findByDateAnd지역코드(String date, String 지역코드);
}
