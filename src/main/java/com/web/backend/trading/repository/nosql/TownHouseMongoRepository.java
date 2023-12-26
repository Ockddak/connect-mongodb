package com.web.backend.trading.repository.nosql;

import com.web.backend.trading.domain.nosql.TownHouse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TownHouseMongoRepository extends MongoRepository<TownHouse, String> {

}
