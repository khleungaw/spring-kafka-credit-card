package com.khleungaw.purchaseprocessor;

import com.khleungaw.purchaseprocessor.config.PropertiesConfig;
import com.khleungaw.purchaseprocessor.model.Purchase;
import com.khleungaw.purchaseprocessor.model.PurchaseWithBalance;
import com.khleungaw.purchaseprocessor.model.PurchaseWithBalanceAndLimit;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PurchaseProcessor {


    private final Logger logger;
    private final PropertiesConfig propertiesConfig;
    private final JsonSerde<Purchase> purchaseSerde;
    private final JsonSerde<BigDecimal> bigDecimalSerde;
    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();


    public PurchaseProcessor(PropertiesConfig propertiesConfig, JsonSerde<Purchase> purchaseSerde, JsonSerde<BigDecimal> bigDecimalSerde) {
        this.logger = LogManager.getLogger();
        this.propertiesConfig = propertiesConfig;
        this.purchaseSerde = purchaseSerde;
        this.bigDecimalSerde = bigDecimalSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        // Prepare stream from purchases, tables from balances and limits
        GlobalKTable<String, BigDecimal> creditCardBalanceTable = streamsBuilder.globalTable(propertiesConfig.getBalanceTopicName(), Consumed.with(STRING_SERDE, bigDecimalSerde));
        GlobalKTable<String, BigDecimal> creditCardLimitTable = streamsBuilder.globalTable(propertiesConfig.getLimitTopicName(), Consumed.with(STRING_SERDE, bigDecimalSerde));
        KStream<String, Purchase> purchaseStream = streamsBuilder.stream(propertiesConfig.getPurchaseTopicName(), Consumed.with(STRING_SERDE, purchaseSerde));
        purchaseStream.peek((cardNo, purchase) -> logger.info("Received purchase: {}", purchase));

        // Intermediate streams
        KStream<String, PurchaseWithBalance> purchaseWithBalanceStream = purchaseStream.leftJoin(
            creditCardBalanceTable,
            (cardNo, Purchase)-> cardNo,
            PurchaseWithBalance::new
        );

        KStream<String, PurchaseWithBalanceAndLimit> purchaseWithBalanceAndLimitStream = purchaseWithBalanceStream.leftJoin(
            creditCardLimitTable,
            (cardNo, purchaseWithBalance) -> cardNo,
            PurchaseWithBalanceAndLimit::new
        );

        // Split the stream into two branches based on new balance
        purchaseWithBalanceAndLimitStream.peek((cardNo, purchaseWithBalanceAndLimit) -> logger.info("Joined purchase: {}", purchaseWithBalanceAndLimit))
            .split()
            .branch(
                (cardNo, purchaseWithBalanceAndLimit) -> {
                    if (purchaseWithBalanceAndLimit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        logger.error("Invalid amount in purchase: {}", purchaseWithBalanceAndLimit);
                        return false;
                    }

                    if (purchaseWithBalanceAndLimit.getBalanceAmount() == null && purchaseWithBalanceAndLimit.getLimitAmount() == null) {
                        logger.info("Unknown card in purchase: {}", purchaseWithBalanceAndLimit);
                        return false;
                    }

                    if (purchaseWithBalanceAndLimit.getBalanceAmount() == null) {
                        logger.error("Missing balance for card in purchase: {}", purchaseWithBalanceAndLimit);
                        return false;
                    }

                    if (purchaseWithBalanceAndLimit.getLimitAmount() == null) {
                        logger.error("Missing limit for card in purchase: {}", purchaseWithBalanceAndLimit);
                        return false;
                    }

                    BigDecimal newBalance = purchaseWithBalanceAndLimit.getBalanceAmount().add(purchaseWithBalanceAndLimit.getAmount());
                    return newBalance.compareTo(purchaseWithBalanceAndLimit.getLimitAmount()) <= 0;
                },
                Branched.withConsumer(stream ->
                    stream.peek((cardNo, purchaseWithBalanceAndLimit) -> logger.info("Accepted purchase: {}", purchaseWithBalanceAndLimit))
                        .mapValues(Purchase::new)
                        .to(propertiesConfig.getAcceptedPurchaseTopicName(), Produced.with(STRING_SERDE, purchaseSerde))
                )
            )
            .defaultBranch(Branched.withConsumer(stream->
                stream.peek((cardNo, purchaseWithBalanceAndLimit) -> logger.info("Rejected purchase: {}", purchaseWithBalanceAndLimit))
                    .mapValues(Purchase::new)
                    .to(propertiesConfig.getRejectedPurchaseTopicName(), Produced.with(STRING_SERDE, purchaseSerde)))
            );
    }

}
