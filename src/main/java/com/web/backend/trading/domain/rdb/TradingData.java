package com.web.backend.trading.domain.rdb;

import com.web.backend.common.domain.CreateTimeEntity;
import com.web.backend.trading.domain.nosql.TownHouse;
import com.web.backend.trading.dto.BuildingType;
import com.web.backend.trading.dto.KakaoAddrResponseDto;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "trading_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradingData extends CreateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String price; //가격
    private String buildingType; //건물타입
    private String fullAddress; //주소
    @Column(name="b_code")
    private String BCode; //법정동코드
    private String pointX; //X좌표
    private String pointY; //Y좌표
    private String buildYear; //건축년도
    private String contractDate; //계약년도
    private String buildingName; //건물명

    public TradingData(TownHouse townHouse, KakaoAddrResponseDto kakaoAddrResponseDto) {
        this.price = townHouse.get거래금액();
        this.buildingType = BuildingType.TOWN_HOUSE.getType();
        this.fullAddress = kakaoAddrResponseDto.getFullAddress();
        this.BCode = kakaoAddrResponseDto.getBCode();
        this.pointX = kakaoAddrResponseDto.getPointX();
        this.pointY = kakaoAddrResponseDto.getPointY();
        this.buildYear = townHouse.get건축년도();
        this.contractDate = townHouse.getDate();
        this.buildingName = townHouse.get연립다세대() + " " + townHouse.get층() + "층";
    }

}
