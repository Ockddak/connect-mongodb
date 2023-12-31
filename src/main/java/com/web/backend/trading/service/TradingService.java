package com.web.backend.trading.service;

import com.web.backend.trading.domain.rdb.TradingData;
import com.web.backend.trading.domain.rdb.TradingHistory;
import com.web.backend.trading.dto.TradingDataRequestDto;
import java.util.List;

public interface TradingService {

    List<String> getPublicData(TradingDataRequestDto tradingDataRequestDto);

    List<TradingData> getProcessingKakao(TradingDataRequestDto tradingDataRequestDto);
}
