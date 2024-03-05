package com.khleungaw.creditcardqueryservice.service;

import com.khleungaw.creditcardqueryservice.config.PropertiesConfig;
import com.khleungaw.creditcardqueryservice.model.CardInfo;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardInfoService {

	private final Logger logger;
	private final ReadOnlyKeyValueStore<String, BigDecimal> balanceStore;
	private final ReadOnlyKeyValueStore<String, BigDecimal> limitStore;

	public CardInfoService(PropertiesConfig propertiesConfig, KafkaStreams kafkaStreams) {
		this.logger = LogManager.getLogger(CardInfoService.class);
		this.balanceStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getBalanceStoreName(), QueryableStoreTypes.keyValueStore()));
		this.limitStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getLimitStoreName(), QueryableStoreTypes.keyValueStore()));
	}

	@Nullable
	public BigDecimal getBalance(String cardNo) {
		return balanceStore.get(cardNo);
	}

	@Nullable
	public BigDecimal getLimit(String cardNo) {
		return limitStore.get(cardNo);
	}

	@Nullable
	public CardInfo getCardInfo(String cardNo) {
		BigDecimal limit = getLimit(cardNo);
		BigDecimal balance = getBalance(cardNo);

		if (balance == null && limit == null) {
			return null;
		}

		if (limit == null) {
			logger.error("Card limit not found for cardNo: {}", cardNo);
			return null;
		}

		if (balance == null) {
			logger.error("Card balance not found for cardNo: {}", cardNo);
			return null;
		}

		return new CardInfo(cardNo, getLimit(cardNo), getBalance(cardNo));
	}

	public List<CardInfo> getAllCardInfo() {
		List<CardInfo> results = new ArrayList<>();

		try (KeyValueIterator<String, BigDecimal> iterator = limitStore.all()) {
			iterator.forEachRemaining(entry -> {
				CardInfo cardInfo = getCardInfo(entry.key);
				if (cardInfo != null) {
					results.add(cardInfo);
				}
			});
		}

		return results;
	}

}
