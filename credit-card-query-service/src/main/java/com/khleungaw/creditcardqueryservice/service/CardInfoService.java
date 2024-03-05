package com.khleungaw.creditcardqueryservice.service;

import com.khleungaw.creditcardqueryservice.config.PropertiesConfig;
import com.khleungaw.creditcardqueryservice.model.CardInfo;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
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

	public KeyValue<String, BigDecimal> getBalance(String cardNo) {
		return KeyValue.pair(cardNo, balanceStore.get(cardNo));
	}

	public Map<String, BigDecimal> getAllBalances() {
		Map<String, BigDecimal> results = new HashMap<>();
		try (KeyValueIterator<String, BigDecimal> iterator = balanceStore.all()) {
			iterator.forEachRemaining(entry -> results.put(entry.key, entry.value));
		}
		return results;
	}

	public KeyValue<String, BigDecimal> getLimit(String cardNo) {
		return KeyValue.pair(cardNo, limitStore.get(cardNo));
	}

	public Map<String, BigDecimal> getAllLimits() {
		Map<String, BigDecimal> results = new HashMap<>();
		try (KeyValueIterator<String, BigDecimal> iterator = limitStore.all()) {
			iterator.forEachRemaining(entry -> results.put(entry.key, entry.value));
		}
		return results;
	}

	public KeyValue<String, CardInfo> getCardInfo(String cardNo) {
		BigDecimal limit = getLimit(cardNo).value;
		BigDecimal balance = getBalance(cardNo).value;

		if (balance == null && limit == null) {
			return KeyValue.pair(cardNo, null);
		}

		if (limit == null) {
			logger.error("Card limit not found for cardNo: {}", cardNo);
			return KeyValue.pair(cardNo, null);
		}

		if (balance == null) {
			logger.error("Card balance not found for cardNo: {}", cardNo);
			return KeyValue.pair(cardNo, null);
		}

		return KeyValue.pair(cardNo, new CardInfo(getLimit(cardNo).value, getBalance(cardNo).value));
	}

	public Map<String, CardInfo> getAllCardInfo() {
		Map<String, CardInfo> results = new HashMap<>();
		Map<String, BigDecimal> balances = getAllBalances();
		Map<String, BigDecimal> limits = getAllLimits();

		for (String cardNo : balances.keySet()) {
			BigDecimal limit = limits.get(cardNo);
			if (limit == null) {
				logger.error("Card limit not found for cardNo: {}", cardNo);
				continue;
			}
			results.put(cardNo, new CardInfo(limit, balances.get(cardNo)));
		}

		for (String cardNo : limits.keySet()) {
			if (results.get(cardNo) == null) {
				logger.error("Card balance not found for cardNo: {}", cardNo);
			}
		}

		return results;
	}

}
