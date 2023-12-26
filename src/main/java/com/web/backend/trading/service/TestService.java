package com.web.backend.trading.service;

import com.web.backend.trading.domain.nosql.Language;
import java.util.List;

public interface TestService {

    List<Language> findAll();
}
