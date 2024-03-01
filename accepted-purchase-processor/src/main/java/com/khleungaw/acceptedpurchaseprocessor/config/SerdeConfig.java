package com.khleungaw.acceptedpurchaseprocessor.config;

import com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustment;
import com.khleungaw.acceptedpurchaseprocessor.model.Purchase;
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
		purchaseSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.acceptedpurchaseprocessor.model.Purchase"
		), false);
		return purchaseSerde;
	}

	@Bean
	public JsonSerde<BalanceAdjustment> balanceAdjustmentSerde() {
		JsonSerde<BalanceAdjustment> balanceAdjustmentSerde = new JsonSerde<>(BalanceAdjustment.class);
		balanceAdjustmentSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "BalanceAdjustment:com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustment"
		), false);
		return balanceAdjustmentSerde;
	}

}
