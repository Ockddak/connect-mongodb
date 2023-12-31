package com.web.backend.trading.domain.nosql;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("plat")
@ToString
@Getter
public class Language {

    @Id
    private String id;
    private String name;
    private String use;
    private String time;
    private String team;
    private String title;
    private String age;
    private String level;
}
