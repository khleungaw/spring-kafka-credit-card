package com.khleungaw.creditcardcardproducer;

import com.khleungaw.creditcardcardproducer.service.CardNoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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

	@Value(value = "${limitTopicName}")
	private String limitTopicName;

	@Value(value = "${balanceTopicName}")
	private String balanceTopicName;

	private final Logger logger = LogManager.getLogger();
	private final KafkaTemplate<String, BigDecimal> balanceKafkaTemplate;
	private final KafkaTemplate<String, BigDecimal> limitKafkaTemplate;
	private final CardNoService cardNoService;

	public CreditCardHandler(KafkaTemplate<String, BigDecimal> balanceKafkaTemplate, KafkaTemplate<String, BigDecimal> limitKafkaTemplate, CardNoService cardNoService) {
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
			}).flatMap(sendResult -> {
				logger.info("Created card: {}", sendResult);
				return ServerResponse.ok().bodyValue(sendResult.getProducerRecord().key());
			}).onErrorResume(e -> {
				logger.error("Failed to create card", e);
				return ServerResponse.badRequest().bodyValue(e.getMessage());
			});
	}

	public Mono<ServerResponse> checkUnique(ServerRequest req) {
		return req.body(BodyExtractors.toMono(String.class))
			.flatMap(cardNo -> {
				logger.info("Received cardNo: {}", cardNo);
				return Mono.just(cardNoService.checkCardNo(cardNo));
			}).flatMap(unique -> {
				logger.info("CardNo is unique: {}", unique);
				return ServerResponse.ok().bodyValue(unique);
			}).onErrorResume(e -> {
				logger.error("Failed to check cardNo", e);
				return ServerResponse.badRequest().bodyValue(e.getMessage());
			});
	}

}