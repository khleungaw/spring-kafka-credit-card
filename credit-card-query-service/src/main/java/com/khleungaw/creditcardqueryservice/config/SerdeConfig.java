package com.khleungaw.creditcardqueryservice.config;

import com.khleungaw.creditcardqueryservice.model.Purchase;
import com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.ArrayList;
import java.util.Map;

@Configuration
public class SerdeConfig {

	@Bean
	public JsonSerde<Purchase> purchaseSerde() {
		JsonSerde<Purchase> purchaseSerde = new JsonSerde<>(Purchase.class);

		purchaseSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.creditcardqueryservice.model.Purchase"
		), false);

		purchaseSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.creditcardqueryservice.model.Purchase"
		), false);

		return purchaseSerde;
	}

	@Bean
	public Serde<ArrayList<PurchaseWithStatus>> purchaseWithStatusSerde() {
		JsonSerde<ArrayList<PurchaseWithStatus>> purchaseWithStatusSerde = new JsonSerde<>(ArrayList.class);

		purchaseWithStatusSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "PurchaseWithStatus:com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus"
		), false);

		purchaseWithStatusSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "PurchaseWithStatus:com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus"
		), false);

		return purchaseWithStatusSerde;
	}

}
