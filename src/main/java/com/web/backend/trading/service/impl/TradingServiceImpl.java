package com.web.backend.trading.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.web.backend.trading.domain.nosql.TownHouse;
import com.web.backend.trading.domain.rdb.TradingData;
import com.web.backend.trading.dto.KakaoAddrResponseDto;
import com.web.backend.trading.dto.TradingDataRequestDto;
import com.web.backend.trading.repository.nosql.TownHouseMongoRepository;
import com.web.backend.trading.repository.rdb.TradingDataRepository;
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
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    @Value("${public-data.service.key}")
    private String SERVICE_KEY;
    @Value("${kakao.address.api.key}")
    private String REST_API_KEY;

    private final TownHouseMongoRepository townHouseMongoRepository;
    private final TradingHistoryRepository tradingHistoryRepository;
    private final TradingDataRepository tradingDataRepository;

    @Override
    @Transactional
    public List<String> getPublicData(TradingDataRequestDto tradingDataRequestDto) {
        List<String> dates = getYyyyMm(tradingDataRequestDto.getStartDate(),
                tradingDataRequestDto.getEndDate());

        List<String> tradingSuccessDates = new ArrayList<>();

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

                if (successYn.equals("Y")) {
                    tradingSuccessDates.add(date);
                }
            }
        } else {
            log.error("getYyyyMm method processing error.");
        }

        return tradingSuccessDates;
    }

    @Override
    @Transactional
    public List<TradingData> getProcessingKakao(TradingDataRequestDto tradingDataRequestDto) {
        List<String> dates = getYyyyMm(tradingDataRequestDto.getStartDate(),
                tradingDataRequestDto.getEndDate());
        List<TradingData> tradingDataList = new ArrayList<>();
        for (String date : dates) {
            List<TradingData> findTradingDataList = tradingDataRepository.findByContractDateAndBCodeStartingWith(
                    date,
                    tradingDataRequestDto.getLawdCd());

            if (!findTradingDataList.isEmpty()) {
                tradingDataList.addAll(findTradingDataList);
                continue;
            }

            List<TownHouse> townHouseList = townHouseMongoRepository.findByDateAnd지역코드(
                    date, tradingDataRequestDto.getLawdCd());

            for (TownHouse townHouse : townHouseList) {
                String townHouseRequestAddress = makeTownHouseRequestAddress(townHouse);
                String kakaoResponse;
                try {
                    kakaoResponse = kakaoAPIRequest(townHouseRequestAddress);
                } catch (IOException e) {
                    log.error("kakaoAPIRequest Error!! {}", e.getMessage());
                    return new ArrayList<>();
                }
                if (!kakaoResponse.equals("not success")) {
                    try {

                        TradingData resultTradingData = getResultTradingData(kakaoResponse,
                                townHouse);
                        if (resultTradingData != null) {
                            tradingDataRepository.save(resultTradingData);
                            tradingDataList.add(resultTradingData);
                        } else {
                            log.error("failed townHouse: {}", makeTownHouseRequestAddress(townHouse));
                        }
                    } catch (JSONException e) {
                        log.error("getResultTradingData Error!! {}", e.getMessage());
                        return new ArrayList<>();
                    }
                }
            }
        }
        return tradingDataList;
    }

    private TradingData getResultTradingData(String kakaoResponse, TownHouse townHouse)
            throws JSONException {
        TradingData tradingData = null;

        JSONObject apiResponse = new JSONObject(kakaoResponse.toString());
        JSONArray documents = new JSONArray(apiResponse.get("documents").toString());

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = (JSONObject) documents.get(i);

            String addressType = String.valueOf(document.get("address_type"));

            if (addressType.equals("REGION_ADDR") || addressType.equals("ROAD_ADDR")) {

                JSONObject address = (JSONObject) document.get("address");
                String bCode = String.valueOf(address.get("b_code"));
                String fullAddress;
                String pointX;
                String pointY;
                if (StringUtils.hasText(bCode) && bCode.startsWith("11350")) {
                    pointX = String.valueOf(document.get("x"));
                    pointY = String.valueOf(document.get("y"));

                    if (addressType.equals("REGION_ADDR")) {
                        fullAddress = String.valueOf(address.get("address_name"));
                    } else {
                        JSONObject roadAddress = (JSONObject) document.get("road_address");
                        fullAddress = String.valueOf(roadAddress.get("address_name"));
                    }

                    KakaoAddrResponseDto kakaoAddrResponseDto = KakaoAddrResponseDto.builder()
                            .fullAddress(fullAddress)
                            .bCode(bCode)
                            .pointX(pointX)
                            .pointY(pointY)
                            .build();
                    tradingData = new TradingData(townHouse, kakaoAddrResponseDto);
                    break;
                }
            }
        }
        return tradingData;
    }

    private String makeTownHouseRequestAddress(TownHouse townHouse) {

        return townHouse.get법정동() + " " + townHouse.get지번() + " " + townHouse.get연립다세대();
    }

    private String kakaoAPIRequest(String requestAddress) throws IOException {
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        String query = requestAddress; // 검색할 주소

        URL url = new URL(apiUrl + "?query=" + URLEncoder.encode(query, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "KakaoAK " + REST_API_KEY);
        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();

        if (responseCode >= 200 && responseCode <= 300) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } else {
            System.out.println("processing not success");
            response.append("not success");
        }

        return response.toString();
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
            int totalCount = Integer.parseInt(String.valueOf(body.path("totalCount")).replace("\"", ""));

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

        if (startYearMonth.isAfter(endYearMonth) || now.isBefore(startYearMonth) || now.isBefore(
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
