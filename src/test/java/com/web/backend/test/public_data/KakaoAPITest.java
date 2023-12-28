package com.web.backend.test.public_data;

import com.web.backend.trading.domain.nosql.TownHouse;
import com.web.backend.trading.domain.rdb.TradingData;
import com.web.backend.trading.dto.KakaoAddrResponseDto;
import com.web.backend.trading.repository.nosql.TownHouseMongoRepository;
import com.web.backend.trading.repository.rdb.TradingDataRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

@SpringBootTest
public class KakaoAPITest {

    @Value("${kakao.address.api.key}")
    private String REST_API_KEY;

    private final TownHouseMongoRepository townHouseMongoRepository;
    private final TradingDataRepository tradingDataRepository;

    @Autowired
    public KakaoAPITest(TownHouseMongoRepository townHouseMongoRepository,
            TradingDataRepository tradingDataRepository) {
        this.townHouseMongoRepository = townHouseMongoRepository;
        this.tradingDataRepository = tradingDataRepository;
    }

    @Test
    @DisplayName("Kakao API 통신 테스트")
    void kakaoAPITest() throws IOException, JSONException {
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        String query = "서울시 마들로 31"; // 검색할 주소

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
            return;
        }

        JSONObject apiResponse = new JSONObject(response.toString());
        JSONArray documents = new JSONArray(apiResponse.get("documents").toString());
        System.out.println("documents = " + documents);
        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = (JSONObject) documents.get(i);
            System.out.println("document = " + document.toString());

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
                    TradingData tradingData = new TradingData(new TownHouse(),
                            kakaoAddrResponseDto);

                    Assertions.assertThat(tradingData.getBCode()).isEqualTo(bCode);
                }
            }
        }
    }

    @Test
    @DisplayName("MongoDB Data Processing Test")
    void MongoDBDataTest() {

        List<TownHouse> townHouseList = townHouseMongoRepository.findByDateAnd지역코드(
                "201512", "11350");
        System.out.println("townHouseList.size() = " + townHouseList.size());
        for (TownHouse townHouse : townHouseList) {
            String townHouseRequestAddress = makeTownHouseRequestAddress(townHouse);
            String kakaoResponse = null;
            try {
                kakaoResponse = kakaoAPIRequest(townHouseRequestAddress);
            } catch (IOException e) {
                System.out.println("asdasd");
            }
            if (StringUtils.hasText(kakaoResponse) && !kakaoResponse.equals("not success")) {
                try {

                    TradingData resultTradingData = getResultTradingData(kakaoResponse,
                            townHouse);
                    if (resultTradingData != null) {
                        tradingDataRepository.save(resultTradingData);
                    } else {
                        System.out.println("failed townHouse:" + makeTownHouseRequestAddress(townHouse));
                    }
                } catch (JSONException e) {
                    System.out.println("aaaaaaa");
                }
            }
        }
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
}
