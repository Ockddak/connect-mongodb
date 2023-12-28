package com.web.backend.trading.dto;

import lombok.Getter;

@Getter
public enum BuildingType {
    TOWN_HOUSE("연립 다세대"),
    APART("아파트");

    private final String type;

    BuildingType(String type) {
        this.type = type;
    }
}
