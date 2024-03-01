package com.khleungaw.creditcardpurchaseprocessor;

import com.khleungaw.creditcardpurchaseprocessor.config.PropertiesConfig;
import com.khleungaw.creditcardpurchaseprocessor.model.Purchase;
import com.khleungaw.creditcardpurchaseprocessor.model.PurchaseWithBalance;
import com.khleungaw.creditcardpurchaseprocessor.model.PurchaseWithBalanceAndLimit;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PurchaseProcessor {

    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();
    private static final JsonSerde<PurchaseWithBalance> PURCHASE_WITH_BALANCE_SERDE = new JsonSerde<>();

    private final Logger logger;
    private final String acceptedPurchaseTopicName;
    private final String balanceTopicName;
    private final String limitTopicName;
    private final String rejectedPurchaseTopicName;
    private final String purchaseTopicName;
    private final JsonSerde<Purchase> purchaseJsonSerde;

    public PurchaseProcessor(PropertiesConfig propertiesConfig, JsonSerde<Purchase> purchaseSerde) {
        this.logger = LogManager.getLogger();
        this.acceptedPurchaseTopicName = propertiesConfig.getAcceptedPurchaseTopicName();
        this.balanceTopicName = propertiesConfig.getBalanceTopicName();
        this.limitTopicName = propertiesConfig.getLimitTopicName();
        this.rejectedPurchaseTopicName = propertiesConfig.getRejectedPurchaseTopicName();
        this.purchaseTopicName = propertiesConfig.getPurchaseTopicName();
        this.purchaseJsonSerde = purchaseSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        // Prepare stream from purchases, tables from balances and limits
        KTable<String, String> creditCardBalanceTable = streamsBuilder.table(balanceTopicName, Consumed.with(STRING_SERDE, STRING_SERDE));
        KTable<String, String> creditCardLimitTable = streamsBuilder.table(limitTopicName, Consumed.with(STRING_SERDE, STRING_SERDE));
        KStream<String, Purchase> purchaseStream = streamsBuilder.stream(purchaseTopicName, Consumed.with(STRING_SERDE, purchaseJsonSerde));
        purchaseStream.foreach((cardNo, purchase) -> logger.info("Received purchase: {}", purchase));

        // Intermediate streams
        KStream<String, PurchaseWithBalance> purchaseWithBalanceStream = purchaseStream
            .leftJoin(creditCardBalanceTable, PurchaseWithBalance::new, Joined.with(STRING_SERDE, purchaseJsonSerde, STRING_SERDE));

        KStream<String, PurchaseWithBalanceAndLimit> purchaseWithBalanceAndLimitStream = purchaseWithBalanceStream
            .leftJoin(creditCardLimitTable, PurchaseWithBalanceAndLimit::new, Joined.with(STRING_SERDE, PURCHASE_WITH_BALANCE_SERDE, STRING_SERDE));

        // Split the stream into two branches based on new balance
        purchaseWithBalanceAndLimitStream.peek((cardNo, purchaseWithBalanceAndLimit) -> logger.info("Joined purchase: {}", purchaseWithBalanceAndLimit))
            .split()
            .branch((cardNo, purchaseWithBalanceAndLimit) -> {
                if (purchaseWithBalanceAndLimit.getBalanceAmount() == null || purchaseWithBalanceAndLimit.getLimitAmount() == null) {
                    logger.warn("Unknown card in purchase: {}", purchaseWithBalanceAndLimit);
                    return false;
                }

                BigDecimal newBalance = purchaseWithBalanceAndLimit.getBalanceAmount().add(purchaseWithBalanceAndLimit.getAmount());
                return newBalance.compareTo(purchaseWithBalanceAndLimit.getLimitAmount()) <= 0;
            }, Branched.withConsumer(stream -> stream
                .mapValues((purchaseWithBalanceAndLimit) -> {
                    logger.info("Accepted purchase: {}", purchaseWithBalanceAndLimit);
                    return new Purchase(purchaseWithBalanceAndLimit);
                }).to(acceptedPurchaseTopicName, Produced.with(STRING_SERDE, purchaseJsonSerde))))
            .defaultBranch(Branched.withConsumer(stream-> stream
                .mapValues((purchaseWithBalanceAndLimit) -> {
                    logger.info("Rejected purchase: {}", purchaseWithBalanceAndLimit);
                    return new Purchase(purchaseWithBalanceAndLimit);
                }).to(rejectedPurchaseTopicName, Produced.with(STRING_SERDE, purchaseJsonSerde))));
    }

}
