package com.khleungaw.creditcardpurchaseprocessor.config;

import com.khleungaw.creditcardpurchaseprocessor.model.Purchase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;

@Configuration
public class SerdeConfig {

	@Bean
	public JsonSerde<Purchase> purchaseSerde() {
		JsonSerde<Purchase> purchaseSerde = new JsonSerde<>(Purchase.class);

		purchaseSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.creditcardpurchaseprocessor.model.Purchase"
		), false);

		purchaseSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.creditcardpurchaseprocessor.model.Purchase"
		), false);
		return purchaseSerde;
	}

}