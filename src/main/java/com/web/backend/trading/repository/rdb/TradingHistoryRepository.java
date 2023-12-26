package com.web.backend.trading.repository.rdb;

import com.web.backend.trading.domain.rdb.TradingHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingHistoryRepository extends JpaRepository<TradingHistory, Long> {

    Optional<TradingHistory> findByDate(String date);
}
