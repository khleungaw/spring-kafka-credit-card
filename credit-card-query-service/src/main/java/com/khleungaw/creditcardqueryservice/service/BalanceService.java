package com.khleungaw.creditcardqueryservice.service;

import com.khleungaw.creditcardqueryservice.config.PropertiesConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BalanceService {

	private final PropertiesConfig propertiesConfig;
	private final KafkaStreams kafkaStreams;

	public BalanceService(PropertiesConfig propertiesConfig, KafkaStreams kafkaStreams) {
		this.propertiesConfig = propertiesConfig;
		this.kafkaStreams = kafkaStreams;
	}

	public BigDecimal getBalance(String cardNo) {
		ReadOnlyKeyValueStore<String, BigDecimal> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getBalanceStoreName(), QueryableStoreTypes.keyValueStore()));
		return store.get(cardNo);
	}

	public List<KeyValue<String, BigDecimal>> getAllBalances() {
		List<KeyValue<String, BigDecimal>> results = new ArrayList<>();
		ReadOnlyKeyValueStore<String, BigDecimal> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getBalanceStoreName(), QueryableStoreTypes.keyValueStore()));
		try (KeyValueIterator<String, BigDecimal> iterator = store.all()) {
			iterator.forEachRemaining(results::add);
		}
		return results;
	}

}
