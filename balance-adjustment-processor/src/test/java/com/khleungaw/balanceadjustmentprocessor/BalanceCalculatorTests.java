package com.khleungaw.balanceadjustmentprocessor;

import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment;
import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustmentType;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.api.MockProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.EXACTLY_ONCE_V2;
import static org.apache.kafka.streams.StreamsConfig.PROCESSING_GUARANTEE_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalanceCalculatorTests {

	final String storeName = "balance-store";
	BalanceCalculator balanceCalculator;
	org.apache.kafka.streams.processor.api.MockProcessorContext<String, BigDecimal> context;

	@BeforeEach
	void setUp() {
		// Set up context
		Properties kafkaStreamConfig = new Properties();
		kafkaStreamConfig.put(APPLICATION_ID_CONFIG, "balance-adjustment-processor-test");
		kafkaStreamConfig.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		kafkaStreamConfig.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2);
		context = new org.apache.kafka.streams.processor.api.MockProcessorContext<>(kafkaStreamConfig);

		// Set up store
		KeyValueStore<String, BigDecimal> balanceStore = Stores.keyValueStoreBuilder(
			Stores.inMemoryKeyValueStore(storeName),
			Serdes.String(),
			new JsonSerde<>(BigDecimal.class)
		).withLoggingDisabled()
		.build();
		balanceStore.init(context.getStateStoreContext(), balanceStore);
		context.getStateStoreContext().register(balanceStore, null);

		// Set up balance calculator
		balanceCalculator = new BalanceCalculator(storeName);
		balanceCalculator.init(context);
	}

	@Test
	@DisplayName("process: should add amount to balance and forward to output topic")
	void testProcess() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal prevBalance = new BigDecimal("1000.00");
		BigDecimal amount = new BigDecimal("100.00");
		BalanceAdjustment balanceAdjustment = new BalanceAdjustment();
		balanceAdjustment.setCardNo(cardNo);
		balanceAdjustment.setAmount(amount);
		balanceAdjustment.setTimestamp(new Date());
		balanceAdjustment.setType(BalanceAdjustmentType.PURCHASE);
		Record<String, BalanceAdjustment> record = new Record<>(cardNo, balanceAdjustment, 0L);

		KeyValueStore<String, BigDecimal> balanceStore = context.getStateStore(storeName);
		balanceStore.put(cardNo, prevBalance);

		// Act
		balanceCalculator.process(record);

		// Assert
		List<MockProcessorContext.CapturedForward<? extends String, ? extends BigDecimal>> forwarded = context.forwarded();
		assertEquals(1, forwarded.size());
		Record<? extends String, ? extends BigDecimal> forwardedRecord = forwarded.get(0).record();
		assertEquals(cardNo, forwardedRecord.key());
		assertEquals(prevBalance.add(amount), forwardedRecord.value());
	}

}
