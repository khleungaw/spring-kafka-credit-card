package com.khleungaw.creditcardproducer;

import com.khleungaw.creditcardproducer.config.PropertiesConfig;
import com.khleungaw.creditcardproducer.service.CardNoService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditCardHandlerTests {

    CreditCardHandler creditCardHandler;

    @Mock KafkaTemplate<String, BigDecimal> balanceKafkaTemplate;
    @Mock KafkaTemplate<String, BigDecimal> limitKafkaTemplate;
    @Mock CardNoService cardNoService;

    @BeforeEach
    void setUp() {
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        propertiesConfig.setPort("80");
        propertiesConfig.setBootstrapAddress("localhost:9092");
        propertiesConfig.setCardNoStoreName("card-no-store");
        propertiesConfig.setLimitTopicName("limitTopic");
        propertiesConfig.setBalanceTopicName("balanceTopic");

        creditCardHandler = new CreditCardHandler(propertiesConfig, balanceKafkaTemplate, limitKafkaTemplate, cardNoService);
    }

    SendResult<String, BigDecimal> createMockSendResult(String topic, String key, BigDecimal value) {
        ProducerRecord<String, BigDecimal> producerRecord = new ProducerRecord<>(topic, key, value);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topic, 1), 0, 0, 0, 0, 0);
        return new SendResult<>(producerRecord, recordMetadata);
    }

    @Test
    @DisplayName("create card: success")
    void testCreateSuccess() {
        // Arrange
        String cardNo = "1234567890";
        BigDecimal limitAmount = new BigDecimal("50000");
        BigDecimal balanceAmount = BigDecimal.ZERO;
        String limitTopicName = "limitTopic";
        String balanceTopicName = "balanceTopic";

        CompletableFuture<SendResult<String, BigDecimal>> limitFuture = CompletableFuture.completedFuture(createMockSendResult(limitTopicName, cardNo, limitAmount));
        CompletableFuture<SendResult<String, BigDecimal>> balanceFuture = CompletableFuture.completedFuture(createMockSendResult(balanceTopicName, cardNo, balanceAmount));

        when(cardNoService.generateCardNo()).thenReturn(cardNo);
        when(limitKafkaTemplate.send(limitTopicName, cardNo, limitAmount)).thenReturn(limitFuture);
        when(balanceKafkaTemplate.send(balanceTopicName, cardNo, balanceAmount)).thenReturn(balanceFuture);
        ServerRequest request = MockServerRequest.builder().body(Mono.just(limitAmount.toString()));

        // Act
        Mono<ServerResponse> responseMono = creditCardHandler.create(request);

        // Assert
        StepVerifier.create(responseMono)
            .expectNextMatches(serverResponse -> {
                assertTrue(serverResponse.statusCode().is2xxSuccessful());
                @SuppressWarnings("unchecked") EntityResponse<String> entityResponse = (EntityResponse<String>) serverResponse;
                assertEquals(cardNo, entityResponse.entity());
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("create card: failure")
    void testCreateFailure() {
        // Arrange
        String cardNo = "1234567890";
        BigDecimal limitAmount = new BigDecimal("50000");
        BigDecimal balanceAmount = BigDecimal.ZERO;
        String limitTopicName = "limitTopic";
        String balanceTopicName = "balanceTopic";

        CompletableFuture<SendResult<String, BigDecimal>> balanceFuture = CompletableFuture.completedFuture(createMockSendResult(balanceTopicName, cardNo, balanceAmount));
        CompletableFuture<SendResult<String, BigDecimal>> limitFuture = CompletableFuture.failedFuture(new RuntimeException("Failed to send limit"));

        when(cardNoService.generateCardNo()).thenReturn(cardNo);
        when(limitKafkaTemplate.send(limitTopicName, cardNo, limitAmount)).thenReturn(limitFuture);
        when(balanceKafkaTemplate.send(balanceTopicName, cardNo, balanceAmount)).thenReturn(balanceFuture);
        ServerRequest request = MockServerRequest.builder().body(Mono.just("50000"));

        // Act
        Mono<ServerResponse> responseMono = creditCardHandler.create(request);

        // Assert
        StepVerifier.create(responseMono)
            .expectNextMatches(serverResponse -> {
                assertTrue(serverResponse.statusCode().is5xxServerError());
                @SuppressWarnings("unchecked") EntityResponse<String> entityResponse = (EntityResponse<String>) serverResponse;
                assertEquals("Failed to send limit", entityResponse.entity());
                return true;
            })
            .verifyComplete();
    }

}
