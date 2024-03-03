package com.khleungaw.creditcardproducer;

import com.khleungaw.creditcardproducer.config.PropertiesConfig;
import com.khleungaw.creditcardproducer.service.CardNoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Component
public class CreditCardHandler {

	private final Logger logger;
	private final String balanceTopicName;
	private final String limitTopicName;
	private final KafkaTemplate<String, BigDecimal> balanceKafkaTemplate;
	private final KafkaTemplate<String, BigDecimal> limitKafkaTemplate;
	private final CardNoService cardNoService;

	public CreditCardHandler(PropertiesConfig propertiesConfig, KafkaTemplate<String, BigDecimal> balanceKafkaTemplate, KafkaTemplate<String, BigDecimal> limitKafkaTemplate, CardNoService cardNoService) {
		this.logger = LogManager.getLogger();
		this.balanceTopicName = propertiesConfig.getBalanceTopicName();
		this.limitTopicName = propertiesConfig.getLimitTopicName();
		this.balanceKafkaTemplate = balanceKafkaTemplate;
		this.limitKafkaTemplate = limitKafkaTemplate;
		this.cardNoService = cardNoService;
	}

	public Mono<ServerResponse> create(ServerRequest req) {
		return req.body(BodyExtractors.toMono(String.class))
			.flatMap(limitString -> {
				logger.info("Received application: {}", limitString);

				// Check if limitString is valid BigDecimal
				try {
					new BigDecimal(limitString);
				} catch (NumberFormatException e) {
					return Mono.error(new IllegalArgumentException("Invalid limit"));
				}

				String cardNo = cardNoService.generateCardNo();
				CompletableFuture<SendResult<String, BigDecimal>> limitFuture = limitKafkaTemplate.send(limitTopicName, cardNo, new BigDecimal(limitString)).toCompletableFuture();
				CompletableFuture<SendResult<String, BigDecimal>> balanceFuture = balanceKafkaTemplate.send(balanceTopicName, cardNo, BigDecimal.ZERO).toCompletableFuture();

				return Mono.fromFuture(CompletableFuture.allOf(limitFuture, balanceFuture).thenApply(v -> limitFuture.join()));
			})
			.flatMap(sendResult -> {
				logger.info("Created card: {}", sendResult);
				return ServerResponse.ok().bodyValue(sendResult.getProducerRecord().key());
			})
			.onErrorResume(e -> {
				logger.error("Failed to create card", e);
				return ServerResponse.status(500).bodyValue(e.getMessage());
			});
	}

}