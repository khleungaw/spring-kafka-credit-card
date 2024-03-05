package com.khleungaw.purchaseproducer;

import com.khleungaw.purchaseproducer.config.PropertiesConfig;
import com.khleungaw.purchaseproducer.model.Purchase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class PurchaseHandler {


    private final Logger logger;
    private final PropertiesConfig propertiesConfig;
    private final KafkaTemplate<String, Purchase> kafkaTemplate;

    public PurchaseHandler(PropertiesConfig propertiesConfig, KafkaTemplate<String, Purchase> kafkaTemplate) {
        this.logger = LogManager.getLogger();
        this.propertiesConfig = propertiesConfig;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        String cardNo = req.pathVariables().get("cardNo");
        return req.body(BodyExtractors.toMono(BigDecimal.class))
            .flatMap(amount -> {
                Purchase purchase = new Purchase(cardNo, amount);
                logger.info("Received purchase: {}", purchase);
                return Mono.fromFuture(kafkaTemplate.send(propertiesConfig.getPurchaseTopicName(), purchase.getCardNo(), purchase).toCompletableFuture());
            })
            .flatMap(sendResult -> {
                logger.info("Sent purchase: {}", sendResult);
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(sendResult.getProducerRecord().value().toString());
            })
            .onErrorResume(e -> {
                logger.error("Failed to send purchase", e);
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
            });
    }

}