package com.khleungaw.creditcardbalanceadjustmentprocessor.config;

import com.khleungaw.creditcardbalanceadjustmentprocessor.model.BalanceAdjustment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
public class SerdeConfig {

	@Bean
	public JsonSerde<BalanceAdjustment> balanceAdjustmentSerde() {
		JsonSerde<BalanceAdjustment> balanceAdjustmentSerde = new JsonSerde<>(BalanceAdjustment.class);
		balanceAdjustmentSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "BalanceAdjustment:com.khleungaw.creditcardbalanceadjustment.model.BalanceAdjustment"
		), false);
		return balanceAdjustmentSerde;
	}

	@Bean JsonSerde<BigDecimal> bigDecimalSerde() {
		return new JsonSerde<>(BigDecimal.class);
	}

}
