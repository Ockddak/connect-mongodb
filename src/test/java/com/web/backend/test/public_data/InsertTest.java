package com.web.backend.test.public_data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.backend.trading.domain.nosql.TownHouse;
import com.web.backend.trading.repository.nosql.TownHouseMongoRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SpringBootTest
public class InsertTest {

    private final String SERVICE_KEY = "wk52r5dzJE%2B%2FE1%2FNlTeXny1gQsMPvFhodPk7aIb6Dm%2FI4nN4MJhEqRN9uAxsH5P7HbvEeD%2BHHFmC8TGr6RIp0A%3D%3D";
    private final TownHouseMongoRepository townHouseMongoRepository;

    @Autowired
    public InsertTest(TownHouseMongoRepository townHouseMongoRepository) {
        this.townHouseMongoRepository = townHouseMongoRepository;
    }

    // XML 문자열을 JSONObject로 변환하는 메서드
    public List<TownHouse> convertXmlStringToJson(String xmlString) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return townHouses;
    }

    @Test
    @DisplayName("xml to json")
    void xmlToJson() throws IOException {

        String requestURL = getRequestURL("111111", "111111");
        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
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

//        while (townHouses.size() <= 10000) {
//            townHouses.addAll(townHouses);
//        }
        System.out.println("townHouses.size() = " + townHouses.size());

        System.out.println("saveAll");
        long start = System.currentTimeMillis();
        townHouseMongoRepository.saveAll(townHouses);
        long end = System.currentTimeMillis();
        System.out.println("end-start = " + (end - start));

//        System.out.println("saveOne");
//        start = System.currentTimeMillis();
//        for (TownHouse townHouse : townHouses) {
//            townHouseMongoRepository.save(townHouse);
//        }
//        end = System.currentTimeMillis();
//        System.out.println("end-start = " + (end - start));

    }

    private String getRequestURL(String startDate, String endDate)
            throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(
                "http://openapi.molit.go.kr:8081/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcRHTrade"); /*URL*/
        urlBuilder.append(
                "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + SERVICE_KEY); /*Service Key*/
        urlBuilder.append(
                "&" + URLEncoder.encode("LAWD_CD", "UTF-8") + "=" + URLEncoder.encode("11350",
                        "UTF-8")); /*각 지역별 코드*/
        urlBuilder.append(
                "&" + URLEncoder.encode("DEAL_YMD", "UTF-8") + "=" + URLEncoder.encode("201512",
                        "UTF-8")); /*월 단위 신고자료*/

        return urlBuilder.toString();
    }

    private List<String> getYYYYMM(String start, String end) {
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
