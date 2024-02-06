package com.khleungaw.creditcardcardproducer.service;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CardNoService {

	@Value(value = "${storeName}")
	private String storeName;

	private final KafkaStreams kafkaStreams;

	public CardNoService(KafkaStreams kafkaStreams) {
		this.kafkaStreams = kafkaStreams;
	}

	public boolean checkCardNo(String cardNo) {
		return kafkaStreams.store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore())).get(cardNo) != null;
	}

	public String generateCardNo() {
		String cardNo;

		do {
			cardNo = String.valueOf((long) (Math.random() * 9_000_000_000_000_000L) + 1_000_000_000_000_000L);
		} while (checkCardNo(cardNo));

		return cardNo;
	}

}
