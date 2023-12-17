package com.web.backend.trading.service.impl;

import com.web.backend.trading.dto.TradingDataRequestDto;
import com.web.backend.trading.service.TradingService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TradingServiceImpl implements TradingService {

    private final String SERVICE_KEY = "wk52r5dzJE%2B%2FE1%2FNlTeXny1gQsMPvFhodPk7aIb6Dm%2FI4nN4MJhEqRN9uAxsH5P7HbvEeD%2BHHFmC8TGr6RIp0A%3D%3D";



    private List<String> getRequestURL(TradingDataRequestDto tradingDataRequestDto)
            throws UnsupportedEncodingException {

        List<String> requestUrls = new ArrayList<>();

        List<String> yyyyMm = getYyyyMm(tradingDataRequestDto.getStartDate(), tradingDataRequestDto.getEndDate());

        for (String date : yyyyMm) {
            StringBuilder urlBuilder = new StringBuilder(
                    "http://openapi.molit.go.kr:8081/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcRHTrade"); /*URL*/
            urlBuilder.append(
                    "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + SERVICE_KEY); /*Service Key*/
            urlBuilder.append(
                    "&" + URLEncoder.encode("LAWD_CD", "UTF-8") + "=" + URLEncoder.encode(tradingDataRequestDto.getLawdCd(),
                            "UTF-8")); /*각 지역별 코드*/
            urlBuilder.append(
                    "&" + URLEncoder.encode("DEAL_YMD", "UTF-8") + "=" + URLEncoder.encode(date,
                            "UTF-8")); /*월 단위 신고자료*/

            requestUrls.add(urlBuilder.toString());
        }

        return requestUrls;
    }

    private List<String> getYyyyMm(String start, String end) {
        if (start.length() != 6 || end.length() != 6) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

        YearMonth startYearMonth = YearMonth.parse(start, formatter);
        YearMonth endYearMonth = YearMonth.parse(end, formatter);

        List<String> yearMonths = new ArrayList<>();
        YearMonth currentYearMonth = startYearMonth;

        while (!currentYearMonth.isAfter(endYearMonth)) {
            yearMonths.add(currentYearMonth.format(formatter));
            currentYearMonth = currentYearMonth.plusMonths(1);
        }

        return yearMonths;
    }
}
