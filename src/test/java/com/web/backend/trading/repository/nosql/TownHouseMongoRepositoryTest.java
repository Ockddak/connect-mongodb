package com.web.backend.trading.repository.nosql;

import static org.junit.jupiter.api.Assertions.*;

import com.web.backend.trading.domain.nosql.TownHouse;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TownHouseMongoRepositoryTest {

    private final TownHouseMongoRepository townHouseMongoRepository;

    @Autowired
    public TownHouseMongoRepositoryTest(TownHouseMongoRepository townHouseMongoRepository) {
        this.townHouseMongoRepository = townHouseMongoRepository;
    }

    @Test
    void findTest() {
        List<TownHouse> townHouseList = townHouseMongoRepository.findByDateAnd지역코드("201512",
                "11350");

        for (TownHouse townHouse : townHouseList) {
            System.out.println("townHouse = " + townHouse.get거래금액());
        }
        System.out.println("townHouseList.size() = " + townHouseList.size());
        Assertions.assertThat(townHouseList.size()).isEqualTo(83);

    }

}