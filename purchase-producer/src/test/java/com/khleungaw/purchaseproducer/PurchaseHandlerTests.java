package com.khleungaw.purchaseproducer;

import com.khleungaw.purchaseproducer.config.PropertiesConfig;
import com.khleungaw.purchaseproducer.model.Purchase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseHandlerTests {

	final String purchaseTopicName = "purchases";
	PurchaseHandler purchaseHandler;
	@Mock KafkaTemplate<String, Purchase> purchaseKafkaTemplate;

	@BeforeEach
	void setUp() {
		PropertiesConfig propertiesConfig = new PropertiesConfig();
		propertiesConfig.setPurchaseTopicName(purchaseTopicName);
		purchaseHandler = new PurchaseHandler(propertiesConfig, purchaseKafkaTemplate);
	}

	SendResult<String, Purchase> createMockSendResult(String key, Purchase value) {
		ProducerRecord<String, Purchase> producerRecord = new ProducerRecord<>(purchaseTopicName, key, value);
		RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(purchaseTopicName, 1), 0, 0, 0, 0, 0);
		return new SendResult<>(producerRecord, recordMetadata);
	}

	@Test
	@DisplayName("create purchase: success")
	void testCreateSuccess() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal(100);
		CompletableFuture<SendResult<String, Purchase>> purchaseFuture = CompletableFuture.completedFuture(createMockSendResult(cardNo, new Purchase(cardNo, amount)));
		when(purchaseKafkaTemplate.send(
			eq(purchaseTopicName),
			argThat(cardNoArg -> cardNoArg.equals(cardNo)),
			argThat(purchase -> purchase.getCardNo().equals(cardNo) && purchase.getAmount().equals(new BigDecimal("100.00"))))
		)
		.thenReturn(purchaseFuture);

		ServerRequest request = MockServerRequest.builder().body(Mono.just(amount));

		// Act
		Mono<ServerResponse> responseMono = purchaseHandler.create(request);

		// Assert
		String expectedEntity = purchaseFuture.join().getProducerRecord().value().toString();
		StepVerifier.create(responseMono)
			.expectNextMatches(serverResponse -> {
				assertTrue(serverResponse.statusCode().is2xxSuccessful());
				@SuppressWarnings("unchecked") EntityResponse<String> entityResponse = (EntityResponse<String>) serverResponse;
				assertEquals(expectedEntity, entityResponse.entity());
				return true;
			})
			.verifyComplete();
	}

	@Test
	@DisplayName("create purchase: failure")
	void testCreateFailure() {
		// Arrange
		String cardNo = "1234567890";
		BigDecimal amount = new BigDecimal(100);
		CompletableFuture<SendResult<String, Purchase>> purchaseFuture = new CompletableFuture<>();
		purchaseFuture.completeExceptionally(new RuntimeException("Failed to send purchase"));
		when(purchaseKafkaTemplate.send(
			eq(purchaseTopicName),
			argThat(cardNoArg -> cardNoArg.equals(cardNo)),
			argThat(purchase -> purchase.getCardNo().equals(cardNo) && purchase.getAmount().equals(new BigDecimal("100.00"))))
		)
		.thenReturn(purchaseFuture);

		ServerRequest request = MockServerRequest.builder().body(Mono.just(amount));

		// Act
		Mono<ServerResponse> responseMono = purchaseHandler.create(request);

		// Assert
		StepVerifier.create(responseMono)
			.expectNextMatches(serverResponse -> {
				assertTrue(serverResponse.statusCode().is5xxServerError());
				@SuppressWarnings("unchecked") EntityResponse<String> entityResponse = (EntityResponse<String>) serverResponse;
				assertEquals("Failed to send purchase", entityResponse.entity());
				return true;
			})
			.verifyComplete();
	}

}
