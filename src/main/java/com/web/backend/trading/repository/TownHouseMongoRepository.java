package com.web.backend.trading.repository;

import com.web.backend.trading.domain.TownHouse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TownHouseMongoRepository extends MongoRepository<TownHouse, String> {

}
