package com.web.backend.trading.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.web.backend.trading.domain.nosql.TownHouse;
import com.web.backend.trading.domain.rdb.TradingHistory;
import com.web.backend.trading.dto.TradingDataRequestDto;
import com.web.backend.trading.repository.nosql.TownHouseMongoRepository;
import com.web.backend.trading.repository.rdb.TradingHistoryRepository;
import com.web.backend.trading.service.TradingService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final String SERVICE_KEY = "wk52r5dzJE%2B%2FE1%2FNlTeXny1gQsMPvFhodPk7aIb6Dm%2FI4nN4MJhEqRN9uAxsH5P7HbvEeD%2BHHFmC8TGr6RIp0A%3D%3D";
    private final TownHouseMongoRepository townHouseMongoRepository;
    private final TradingHistoryRepository tradingHistoryRepository;

    @Override
    public List<TradingHistory> getPublicData(TradingDataRequestDto tradingDataRequestDto) {
        List<String> dates = getYyyyMm(tradingDataRequestDto.getStartDate(),
                tradingDataRequestDto.getEndDate());

        List<TradingHistory> tradingHistories = new ArrayList<>();

        if (!dates.isEmpty()) {
            for (String date : dates) {
                String requestURL = getRequestURL(date, tradingDataRequestDto.getLawdCd());
                String successYn;
                try {
                    successYn = publicDataInsert(requestURL);
                } catch (IOException e) {
                    log.error("{} is error search API", date);
                    successYn = "N";
                }
            }
        } else {
            log.error("getYyyyMm method processing error.");
        }

        return tradingHistories;
    }

    private String publicDataInsert(String requestURL) throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            return "N";
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // StringBuilder에 저장된 XML 데이터를 문자열로 변환
        String xmlData = sb.toString();
        System.out.println("xmlData = " + xmlData);

        List<TownHouse> townHouses = convertXmlStringToJson(xmlData);
        townHouseMongoRepository.saveAll(townHouses);

        return "Y";
    }

    private List<TownHouse> convertXmlStringToJson(String xmlString) {
        List<TownHouse> townHouses = new ArrayList<>();
        try {
            // XmlMapper를 생성하여 XML 파싱
            ObjectMapper xmlMapper = new XmlMapper();
            JsonNode jsonNode = xmlMapper.readTree(xmlString.getBytes());
            JsonNode body = jsonNode.path("body");
            JsonNode items = body.path("items");
            int totalCount = Integer.parseInt(body.path("totalCount").toString());

            JsonNode item = items.get("item");

            // JsonNode를 JSONObject로 변환
            ObjectMapper objectMapper = new ObjectMapper();

            for (JsonNode node : item) {
                TownHouse townHouse = objectMapper.readValue(node.toString(), TownHouse.class);
                townHouse.trimAll();
                townHouses.add(townHouse);
            }

            if (totalCount != townHouses.size()) {
                log.error("totalCount: {}, townHouseSize: {}", totalCount, townHouses.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return townHouses;
    }

    private String getRequestURL(String date, String lawdCd) {

        try {
            StringBuilder urlBuilder = new StringBuilder(
                    "http://openapi.molit.go.kr:8081/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcRHTrade"); /*URL*/
            urlBuilder.append(
                    "?" + URLEncoder.encode("serviceKey", "UTF-8") + "="
                            + SERVICE_KEY); /*Service Key*/
            urlBuilder.append(
                    "&" + URLEncoder.encode("LAWD_CD", "UTF-8") + "=" + URLEncoder.encode(
                            lawdCd,
                            "UTF-8")); /*각 지역별 코드*/
            urlBuilder.append(
                    "&" + URLEncoder.encode("DEAL_YMD", "UTF-8") + "=" + URLEncoder.encode(date,
                            "UTF-8")); /*월 단위 신고자료*/

            return urlBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding Error: {}, message: {}", date, e.getMessage());
            return "encodeError";
        }

    }

    private List<String> getYyyyMm(String start, String end) {
        List<String> yearMonths = new ArrayList<>();

        if (start.length() != 6 || end.length() != 6) {
            return yearMonths;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

        YearMonth startYearMonth = YearMonth.parse(start, formatter);
        YearMonth endYearMonth = YearMonth.parse(end, formatter);
        YearMonth now = YearMonth.now();

        if (startYearMonth.isAfter(endYearMonth) || now.isAfter(startYearMonth) || now.isAfter(
                endYearMonth)) {
            return yearMonths;
        }

        YearMonth currentYearMonth = startYearMonth;

        while (!currentYearMonth.isAfter(endYearMonth)) {
            yearMonths.add(currentYearMonth.format(formatter));
            currentYearMonth = currentYearMonth.plusMonths(1);
        }

        return yearMonths;
    }

}
