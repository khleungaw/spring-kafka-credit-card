package com.khleungaw.creditcardpurchaseproducer;

import com.khleungaw.creditcardpurchaseproducer.model.Purchase;
import com.khleungaw.creditcardpurchaseproducer.model.PurchaseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PurchaseHandler {

    @Value(value = "${purchaseTopicName}")
    private String purchaseTopicName;

    private final Logger logger;
    private final KafkaTemplate<String, Purchase> kafkaTemplate;

    public PurchaseHandler(KafkaTemplate<String, Purchase> kafkaTemplate) {
        this.logger = LogManager.getLogger();
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.body(BodyExtractors.toMono(PurchaseDTO.class))
                .flatMap(purchaseDTO -> {
                    Purchase purchase = new Purchase(purchaseDTO);
                    logger.info("Received purchase: {}", purchase);
                    return Mono.fromFuture(kafkaTemplate.send(purchaseTopicName, purchase.getCardNo(), purchase).toCompletableFuture());
                }).flatMap(sendResult -> {
                    logger.info("Sent purchase: {}", sendResult);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(sendResult.getProducerRecord().value().toString());
                }).onErrorResume(e -> {
                    logger.error("Failed to send purchase", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
                });
    }

}