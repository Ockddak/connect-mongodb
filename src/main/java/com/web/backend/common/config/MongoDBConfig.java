package com.web.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.web.backend.trading.repository.nosql"})
public class MongoDBConfig {

}
