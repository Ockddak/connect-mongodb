package com.web.backend.trading.repository.rdb;

import com.web.backend.trading.domain.rdb.TradingHistory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
class TradingHistoryRepositoryTest {

    private final TradingHistoryRepository tradingHistoryRepository;

    private final PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @Autowired
    public TradingHistoryRepositoryTest(TradingHistoryRepository tradingHistoryRepository,
            PlatformTransactionManager platformTransactionManager) {
        this.tradingHistoryRepository = tradingHistoryRepository;
        this.transactionManager = platformTransactionManager;
    }

    @BeforeEach
    public void initTransaction() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @DisplayName("트랜잭션 미사용")
    void saveTestNotDelete() {
        //given
        TradingHistory tradingHistory = TradingHistory.builder()
                .domainName("test")
                .endYn("Y")
                .date("202312")
                .build();

        //when
        TradingHistory save = tradingHistoryRepository.save(tradingHistory);

        //then
        Assertions.assertThat(save.getDomainName()).isEqualTo(tradingHistory.getDomainName());

    }

    @Test
    @DisplayName("트랜잭션 사용")
    void saveTestUseDelete() {
        TradingHistory savedHistory = transactionTemplate.execute(status -> {
            //given
            TradingHistory tradingHistory = TradingHistory.builder()
                    .domainName("useDelete")
                    .endYn("Y")
                    .date("202312")
                    .build();

            //when
            TradingHistory save = tradingHistoryRepository.save(tradingHistory);

            return save;
        });

        //then
        Assertions.assertThat(savedHistory.getDomainName()).isEqualTo("useDelete");

    }

}