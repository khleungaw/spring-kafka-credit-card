package com.khleungaw.acceptedpurchaseprocessor;

import com.khleungaw.acceptedpurchaseprocessor.config.PropertiesConfig;
import com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustment;
import com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustmentType;
import com.khleungaw.acceptedpurchaseprocessor.model.Purchase;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.EXACTLY_ONCE_V2;
import static org.apache.kafka.streams.StreamsConfig.PROCESSING_GUARANTEE_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcceptedPurchaseProcessorTests {

	AcceptedPurchaseProcessor acceptedPurchaseProcessor;
	TopologyTestDriver testDriver;
	TestInputTopic<String, Purchase> acceptedPurchaseTopicInput;
	TestOutputTopic<String, BalanceAdjustment> balanceAdjustmentTopicOutput;

	@BeforeEach
	void setUp() {
		// Set up Processor
		PropertiesConfig propertiesConfig = new PropertiesConfig();
		propertiesConfig.setAcceptedPurchaseTopicName("accepted-purchases");
		propertiesConfig.setBalanceAdjustmentTopicName("balance-adjustments");

		JsonSerde<Purchase> purchaseSerde = new JsonSerde<>(Purchase.class);
		purchaseSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.acceptedpurchaseprocessor.model.Purchase"
		), false);

		JsonSerde<BalanceAdjustment> balanceAdjustmentSerde = new JsonSerde<>(BalanceAdjustment.class);
		balanceAdjustmentSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "BalanceAdjustment:com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustment"
		), false);
		balanceAdjustmentSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "BalanceAdjustment:com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustment"
		), false);

		acceptedPurchaseProcessor = new AcceptedPurchaseProcessor(propertiesConfig, purchaseSerde, balanceAdjustmentSerde);

		// Set up Kafka Stream config
		Properties kafkaStreamConfig = new Properties();
		kafkaStreamConfig.put(APPLICATION_ID_CONFIG, "accepted-purchase-processor-test");
		kafkaStreamConfig.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		kafkaStreamConfig.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2);

		// Set up test topology
		StreamsBuilder builder = new StreamsBuilder();
		acceptedPurchaseProcessor.buildPipeline(builder);
		testDriver = new TopologyTestDriver(builder.build(), kafkaStreamConfig);

		// Create test topics
		acceptedPurchaseTopicInput = testDriver.createInputTopic(propertiesConfig.getAcceptedPurchaseTopicName(), Serdes.String().serializer(), purchaseSerde.serializer());
		balanceAdjustmentTopicOutput = testDriver.createOutputTopic(propertiesConfig.getBalanceAdjustmentTopicName(), Serdes.String().deserializer(), balanceAdjustmentSerde.deserializer());
	}

	@AfterEach
	void tearDown() {
		testDriver.close();
	}

	@Test
	@DisplayName("process purchase: creates valid balance adjustment")
	void testPipeline() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal("100.00");
		Purchase purchase = new Purchase();
		purchase.setId(UUID.randomUUID());
		purchase.setCardNo(cardNo);
		purchase.setAmount(amount);
		purchase.setTimestamp(new Date());

		// Act
		acceptedPurchaseTopicInput.pipeInput(cardNo, purchase);

		// Assert
		List<BalanceAdjustment> balanceAdjustments = balanceAdjustmentTopicOutput.readValuesToList();
		assertEquals(1, balanceAdjustments.size());
		BalanceAdjustment balanceAdjustment = balanceAdjustments.get(0);
		assertEquals(cardNo, balanceAdjustment.getCardNo());
		assertEquals(amount, balanceAdjustment.getAmount());
		assertEquals(BalanceAdjustmentType.PURCHASE, balanceAdjustment.getType());
	}

}
