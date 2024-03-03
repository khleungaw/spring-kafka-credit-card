package com.khleungaw.balanceadjustmentprocessor;

import com.khleungaw.balanceadjustmentprocessor.config.PropertiesConfig;
import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment;
import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustmentType;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.EXACTLY_ONCE_V2;
import static org.apache.kafka.streams.StreamsConfig.PROCESSING_GUARANTEE_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalanceAdjustmentProcessorTests {

	BalanceAdjustmentProcessor balanceAdjustmentProcessor;
	TopologyTestDriver testDriver;
	TestInputTopic<String, BalanceAdjustment> balanceAdjustmentTopicInput;
	TestOutputTopic<String, BigDecimal> balanceTopicOutput;

	@BeforeEach
	void setUp() {
		// Set up Processor
		PropertiesConfig propertiesConfig = new PropertiesConfig();
		propertiesConfig.setBootstrapAddress("kafka:9092");
		propertiesConfig.setBalanceStoreName("balance-store");
		propertiesConfig.setBalanceTopicName("balances");
		propertiesConfig.setBalanceAdjustmentTopicName("balance-adjustments");

		JsonSerde<BalanceAdjustment> balanceAdjustmentSerde = new JsonSerde<>(BalanceAdjustment.class);
		balanceAdjustmentSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "BalanceAdjustment:com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment"
		), false);

		balanceAdjustmentSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "BalanceAdjustment:com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment"
		), false);

		JsonSerde<BigDecimal> bigDecimalSerde = new JsonSerde<>(BigDecimal.class);
		balanceAdjustmentProcessor = new BalanceAdjustmentProcessor(propertiesConfig, balanceAdjustmentSerde, bigDecimalSerde);

		// Set up Kafka stream config
		Properties kafkaStreamConfig = new Properties();
		kafkaStreamConfig.put(APPLICATION_ID_CONFIG, "balance-adjustment-processor-test");
		kafkaStreamConfig.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		kafkaStreamConfig.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2);

		// Set up test topology
		StreamsBuilder builder = new StreamsBuilder();
		balanceAdjustmentProcessor.buildPipeline(builder);
		testDriver = new TopologyTestDriver(builder.build(), kafkaStreamConfig);

		// Create test topics
		balanceAdjustmentTopicInput = testDriver.createInputTopic(propertiesConfig.getBalanceAdjustmentTopicName(), Serdes.String().serializer(), balanceAdjustmentSerde.serializer());
		balanceTopicOutput = testDriver.createOutputTopic(propertiesConfig.getBalanceTopicName(), Serdes.String().deserializer(), bigDecimalSerde.deserializer());
	}

	@AfterEach
	void tearDown() {
		testDriver.close();
	}

	@Test
	@DisplayName("new balance adjustment: correct new balance")
	void testPipeline() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal prevBalance = new BigDecimal("1000.00");
		BigDecimal amount = new BigDecimal("100.00");
		BalanceAdjustment balanceAdjustment = new BalanceAdjustment();
		balanceAdjustment.setCardNo(cardNo);
		balanceAdjustment.setAmount(amount);
		balanceAdjustment.setTimestamp(new Date());
		balanceAdjustment.setType(BalanceAdjustmentType.PURCHASE);

		// Act
		KeyValueStore<String, BigDecimal> balanceStore = testDriver.getKeyValueStore("balance-store");
		balanceStore.put(cardNo, prevBalance);
		balanceAdjustmentTopicInput.pipeInput(cardNo, balanceAdjustment);

		// Assert
		BigDecimal expectedBalanceInStateStore = prevBalance.add(amount);
		BigDecimal actualBalanceInStateStore = (BigDecimal) testDriver.getKeyValueStore("balance-store").get(cardNo);
		assertEquals(expectedBalanceInStateStore, actualBalanceInStateStore);

		BigDecimal expectedBalance = prevBalance.add(amount);
		BigDecimal actualBalance = balanceTopicOutput.readValue();
		assertEquals(expectedBalance, actualBalance);
	}

}
