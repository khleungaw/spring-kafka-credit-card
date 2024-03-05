package com.khleungaw.creditcardqueryservice.service;

import com.khleungaw.creditcardqueryservice.config.PropertiesConfig;
import com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardPurchaseService {

	private final ReadOnlyKeyValueStore<String, List<PurchaseWithStatus>> store;

	public CardPurchaseService(PropertiesConfig propertiesConfig, KafkaStreams kafkaStreams) {
		this.store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(propertiesConfig.getPurchaseWithStatusStoreName(), QueryableStoreTypes.keyValueStore()));
	}

	@Nullable
	public List<PurchaseWithStatus> getCardPurchases(String cardNo) {
		return store.get(cardNo);
	}

	public List<PurchaseWithStatus> getAllCardPurchases() {
		List<PurchaseWithStatus> cardPurchases = new ArrayList<>();

		try (KeyValueIterator<String, List<PurchaseWithStatus>> allCardPurchases = store.all()) {
			while (allCardPurchases.hasNext()) {
				KeyValue<String, List<PurchaseWithStatus>> cardPurchase = allCardPurchases.next();
				cardPurchases.addAll(cardPurchase.value);
			}
		}

		return cardPurchases;
	}

}
