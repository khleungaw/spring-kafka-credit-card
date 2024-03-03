package com.khleungaw.purchaseprocessor;

import com.khleungaw.purchaseprocessor.config.PropertiesConfig;
import com.khleungaw.purchaseprocessor.model.Purchase;
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

public class PurchaseProcessorTests {

	final String acceptedPurchaseTopicName = "accepted-purchases";
	final String balanceTopicName = "balances";
	final String limitTopicName = "limits";
	final String rejectedPurchaseTopicName = "rejected-purchases";
	final String purchaseTopicName = "purchases";

	PurchaseProcessor purchaseProcessor;
	TopologyTestDriver testDriver;
	TestInputTopic<String, Purchase> purchaseTopicInput;
	TestInputTopic<String, BigDecimal> balanceTopicInput;
	TestInputTopic<String, BigDecimal> limitTopicInput;
	TestOutputTopic<String, Purchase> acceptedPurchaseTopicOutput;
	TestOutputTopic<String, Purchase> rejectedPurchaseTopicOutput;


	@BeforeEach
	public void setUp() {
		// Set up processor
		PropertiesConfig propertiesConfig = new PropertiesConfig();
		propertiesConfig.setBootstrapAddress("kafka:9092");
		propertiesConfig.setAcceptedPurchaseTopicName(acceptedPurchaseTopicName);
		propertiesConfig.setBalanceTopicName(balanceTopicName);
		propertiesConfig.setLimitTopicName(limitTopicName);
		propertiesConfig.setRejectedPurchaseTopicName(rejectedPurchaseTopicName);
		propertiesConfig.setPurchaseTopicName(purchaseTopicName);

		JsonSerde<Purchase> purchaseSerde = new JsonSerde<>(Purchase.class);
		purchaseSerde.serializer().configure(Map.of(
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.purchaseprocessor.model.Purchase"
		), false);
		purchaseSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "com.khleungaw.*",
				JsonDeserializer.TYPE_MAPPINGS, "Purchase:com.khleungaw.purchaseprocessor.model.Purchase"
		), false);

		JsonSerde<BigDecimal> bigDecimalSerde = new JsonSerde<>(BigDecimal.class);
		bigDecimalSerde.deserializer().configure(Map.of(
				JsonDeserializer.TRUSTED_PACKAGES, "java.math.*"
		), false);

		purchaseProcessor = new PurchaseProcessor(propertiesConfig, purchaseSerde, bigDecimalSerde);

		// Set up Kafka Stream config
		Properties kafkaStreamConfig = new Properties();
		kafkaStreamConfig.put(APPLICATION_ID_CONFIG, "purchase-processor-test");
		kafkaStreamConfig.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		kafkaStreamConfig.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		kafkaStreamConfig.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2);

		// Set up test topology
		StreamsBuilder builder = new StreamsBuilder();
		purchaseProcessor.buildPipeline(builder);
		testDriver = new TopologyTestDriver(builder.build(), kafkaStreamConfig);

		// Set up test topics
		balanceTopicInput = testDriver.createInputTopic(propertiesConfig.getBalanceTopicName(), Serdes.String().serializer(), bigDecimalSerde.serializer());
		limitTopicInput = testDriver.createInputTopic(propertiesConfig.getLimitTopicName(), Serdes.String().serializer(), bigDecimalSerde.serializer());
		purchaseTopicInput = testDriver.createInputTopic(propertiesConfig.getPurchaseTopicName(), Serdes.String().serializer(), purchaseSerde.serializer());
		acceptedPurchaseTopicOutput = testDriver.createOutputTopic(propertiesConfig.getAcceptedPurchaseTopicName(), Serdes.String().deserializer(), purchaseSerde.deserializer());
		rejectedPurchaseTopicOutput = testDriver.createOutputTopic(propertiesConfig.getRejectedPurchaseTopicName(), Serdes.String().deserializer(), purchaseSerde.deserializer());
	}

	@AfterEach
	public void tearDown() {
		testDriver.close();
	}

	@Test
	@DisplayName("process purchase: valid card and within limit")
	public void testValidCardAndWithinLimit() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal("0.01");
		BigDecimal currentBalance = new BigDecimal("999.99");
		BigDecimal limit = new BigDecimal("1000.00");
		Purchase purchase = new Purchase();
		purchase.setId(UUID.randomUUID());
		purchase.setCardNo(cardNo);
		purchase.setAmount(amount);
		purchase.setTimestamp(new Date());

		balanceTopicInput.pipeInput(cardNo, currentBalance);
		limitTopicInput.pipeInput(cardNo, limit);

		// Act
		purchaseTopicInput.pipeInput(cardNo, purchase);

		// Assert
		List<Purchase> actualRejectedPurchases = rejectedPurchaseTopicOutput.readValuesToList();
		assertEquals(0, actualRejectedPurchases.size());

		List<Purchase> actualAcceptedPurchases = acceptedPurchaseTopicOutput.readValuesToList();
		assertEquals(1, actualAcceptedPurchases.size());
		assertEquals(purchase.getId(), actualAcceptedPurchases.get(0).getId());
		assertEquals(purchase.getCardNo(), actualAcceptedPurchases.get(0).getCardNo());
		assertEquals(purchase.getAmount(), actualAcceptedPurchases.get(0).getAmount());
		assertEquals(purchase.getTimestamp(), actualAcceptedPurchases.get(0).getTimestamp());
	}

	@Test
	@DisplayName("process purchase: valid card and over limit")
	public void testValidCardAndOverLimit() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal("0.01");
		BigDecimal currentBalance = new BigDecimal("999.99");
		BigDecimal limit = new BigDecimal("999.99");
		Purchase purchase = new Purchase();
		purchase.setId(UUID.randomUUID());
		purchase.setCardNo(cardNo);
		purchase.setAmount(amount);
		purchase.setTimestamp(new Date());

		balanceTopicInput.pipeInput(cardNo, currentBalance);
		limitTopicInput.pipeInput(cardNo, limit);

		// Act
		purchaseTopicInput.pipeInput(cardNo, purchase);

		// Assert
		List<Purchase> actualAcceptedPurchases = acceptedPurchaseTopicOutput.readValuesToList();
		assertEquals(0, actualAcceptedPurchases.size());

		List<Purchase> actualRejectedPurchases = rejectedPurchaseTopicOutput.readValuesToList();
		assertEquals(1, actualRejectedPurchases.size());
		assertEquals(purchase.getId(), actualRejectedPurchases.get(0).getId());
		assertEquals(purchase.getCardNo(), actualRejectedPurchases.get(0).getCardNo());
		assertEquals(purchase.getAmount(), actualRejectedPurchases.get(0).getAmount());
		assertEquals(purchase.getTimestamp(), actualRejectedPurchases.get(0).getTimestamp());
	}

	@Test
	@DisplayName("process purchase: unknown card")
	public void testUnknownCard() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal("0.01");
		Purchase purchase = new Purchase();
		purchase.setId(UUID.randomUUID());
		purchase.setCardNo(cardNo);
		purchase.setAmount(amount);
		purchase.setTimestamp(new Date());

		// Act
		purchaseTopicInput.pipeInput(cardNo, purchase);

		// Assert
		List<Purchase> actualAcceptedPurchases = acceptedPurchaseTopicOutput.readValuesToList();
		assertEquals(0, actualAcceptedPurchases.size());

		List<Purchase> actualRejectedPurchases = rejectedPurchaseTopicOutput.readValuesToList();
		assertEquals(1, actualRejectedPurchases.size());
		assertEquals(purchase.getId(), actualRejectedPurchases.get(0).getId());
		assertEquals(purchase.getCardNo(), actualRejectedPurchases.get(0).getCardNo());
		assertEquals(purchase.getAmount(), actualRejectedPurchases.get(0).getAmount());
		assertEquals(purchase.getTimestamp(), actualRejectedPurchases.get(0).getTimestamp());
	}

	@Test
	@DisplayName("process purchase: missing balance")
	public void testMissingBalance() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal("0.01");
		BigDecimal limit = new BigDecimal("1000.00");
		Purchase purchase = new Purchase();
		purchase.setId(UUID.randomUUID());
		purchase.setCardNo(cardNo);
		purchase.setAmount(amount);
		purchase.setTimestamp(new Date());

		limitTopicInput.pipeInput(cardNo, limit);

		// Act
		purchaseTopicInput.pipeInput(cardNo, purchase);

		// Assert
		List<Purchase> actualAcceptedPurchases = acceptedPurchaseTopicOutput.readValuesToList();
		assertEquals(0, actualAcceptedPurchases.size());

		List<Purchase> actualRejectedPurchases = rejectedPurchaseTopicOutput.readValuesToList();
		assertEquals(1, actualRejectedPurchases.size());
		assertEquals(purchase.getId(), actualRejectedPurchases.get(0).getId());
		assertEquals(purchase.getCardNo(), actualRejectedPurchases.get(0).getCardNo());
		assertEquals(purchase.getAmount(), actualRejectedPurchases.get(0).getAmount());
		assertEquals(purchase.getTimestamp(), actualRejectedPurchases.get(0).getTimestamp());
	}

	@Test
	@DisplayName("process purchase: missing limit")
	public void testMissingLimit() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal("0.01");
		BigDecimal currentBalance = new BigDecimal("999.99");
		Purchase purchase = new Purchase();
		purchase.setId(UUID.randomUUID());
		purchase.setCardNo(cardNo);
		purchase.setAmount(amount);
		purchase.setTimestamp(new Date());

		balanceTopicInput.pipeInput(cardNo, currentBalance);

		// Act
		purchaseTopicInput.pipeInput(cardNo, purchase);

		// Assert
		List<Purchase> actualAcceptedPurchases = acceptedPurchaseTopicOutput.readValuesToList();
		assertEquals(0, actualAcceptedPurchases.size());

		List<Purchase> actualRejectedPurchases = rejectedPurchaseTopicOutput.readValuesToList();
		assertEquals(1, actualRejectedPurchases.size());
		assertEquals(purchase.getId(), actualRejectedPurchases.get(0).getId());
		assertEquals(purchase.getCardNo(), actualRejectedPurchases.get(0).getCardNo());
		assertEquals(purchase.getAmount(), actualRejectedPurchases.get(0).getAmount());
		assertEquals(purchase.getTimestamp(), actualRejectedPurchases.get(0).getTimestamp());
	}

}
