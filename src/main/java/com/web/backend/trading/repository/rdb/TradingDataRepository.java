package com.web.backend.trading.repository.rdb;

import com.web.backend.trading.domain.rdb.TradingData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingDataRepository extends JpaRepository<TradingData, Long> {

    List<TradingData> findByContractDateAndBCodeStartingWith(String contractDate, String bCode);
}
