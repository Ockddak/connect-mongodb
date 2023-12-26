package com.web.backend.trading.domain.rdb;

import com.web.backend.common.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Kakao 주소정제 API를 거치고 난 후의 히스토리 데이터를 관리
 */
@Entity(name="trading_history_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradingHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String date;
    @Column(nullable = false)
    private String domainName;
    @Column(nullable = false)
    private String endYn;

    @Builder
    public TradingHistory(String date, String domainName, String endYn) {
        this.date = date;
        this.domainName = domainName;
        this.endYn = endYn;
    }

}
