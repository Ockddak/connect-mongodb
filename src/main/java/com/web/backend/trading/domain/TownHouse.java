package com.web.backend.trading.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document("town_house")
public class TownHouse {

    @Id
    private String id;
    private String 거래금액;
    private String 거래유형;
    private String 건축년도;
    private String 년;
    private String 대지권면적;
    private String 법정동;
    private String 연립다세대;
    private String 월;
    private String 일;
    private String 전용면적;
    private String 중개사소재지;
    private String 지번;
    private String 지역코드;
    private String 층;
    private String 해제사유발생일;
    private String 해제여부;

    public void trimAll() {
        this.거래금액 = this.거래금액.trim();
        this.거래유형 = this.거래유형.trim();
        this.건축년도 = this.건축년도.trim();
        this.년 = this.년.trim();
        this.대지권면적 = this.대지권면적.trim();
        this.법정동 = this.법정동.trim();
        this.연립다세대 = this.연립다세대.trim();
        this.월 = this.월.trim();
        this.일 = this.일.trim();
        this.전용면적 = this.전용면적.trim();
        this.중개사소재지 = this.중개사소재지.trim();
        this.지번 = this.지번.trim();
        this.지역코드 = this.지역코드.trim();
        this.층 = this.층.trim();
        this.해제사유발생일 = this.해제사유발생일.trim();
        this.해제여부 = this.해제여부.trim();
    }
}
