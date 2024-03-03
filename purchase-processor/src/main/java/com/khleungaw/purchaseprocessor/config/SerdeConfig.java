package com.khleungaw.purchaseprocessor.config;

import com.khleungaw.purchaseprocessor.model.Purchase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
public class SerdeConfig {

	@Bean
	public JsonSerde<Purchase> purchaseSerde() {
		JsonSerde<Purchase> purchaseSerde = new JsonSerde<>(Purchase.class);

		purchaseSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.purchaseprocessor.model.Purchase"
		), false);

		purchaseSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.purchaseprocessor.model.Purchase"
		), false);

		return purchaseSerde;
	}

	@Bean
	public JsonSerde<BigDecimal> bigDecimalSerde() {
		JsonSerde<BigDecimal> bigDecimalSerde = new JsonSerde<>(BigDecimal.class);
		bigDecimalSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "java.math.*"
		), false);
		return bigDecimalSerde;
	}

}