package com.khleungaw.creditcardqueryservice.service;

import com.khleungaw.creditcardqueryservice.config.PropertiesConfig;
import com.khleungaw.creditcardqueryservice.model.Purchase;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AcceptedPurchaseService {

	private final PropertiesConfig propertiesConfig;
	private final KafkaStreams kafkaStreams;

	public AcceptedPurchaseService(PropertiesConfig propertiesConfig, KafkaStreams kafkaStreams) {
		this.propertiesConfig = propertiesConfig;
		this.kafkaStreams = kafkaStreams;
	}

	public List<Purchase> getAcceptedPurchaseOfCard(String cardNo) {
		List<Purchase> result = new ArrayList<>();
		ReadOnlyWindowStore<String, Purchase> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getAcceptedPurchaseStoreName(), QueryableStoreTypes.windowStore()));
		try (WindowStoreIterator<Purchase> iterator = store.fetch(cardNo, Instant.MIN, Instant.now())) {
			iterator.forEachRemaining(pair -> result.add(pair.value));
		}
		return result;
	}

	public List<KeyValue<String, Purchase>> getAllAcceptedPurchases() {
		List<KeyValue<String, Purchase>> results = new ArrayList<>();
		ReadOnlyKeyValueStore<String, Purchase> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getBalanceStoreName(), QueryableStoreTypes.keyValueStore()));
		try (KeyValueIterator<String, Purchase> iterator = store.all()) {
			iterator.forEachRemaining(results::add);
		}
		return results;
	}

}
