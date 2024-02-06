package com.khleungaw.creditcardacceptedpurchaseprocessor;

import com.khleungaw.creditcardacceptedpurchaseprocessor.model.BalanceAdjustment;
import com.khleungaw.creditcardacceptedpurchaseprocessor.model.Purchase;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class AcceptedPurchaseProcessor {

    @Value(value = "${balanceAdjustmentTopicName}")
    private String balanceAdjustmentTopicName;

    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();
    private final Logger logger;
    private final JsonSerde<Purchase> purchaseJsonSerde;

    public AcceptedPurchaseProcessor(JsonSerde<Purchase> purchaseSerde) {
        this.logger = LogManager.getLogger();
        this.purchaseJsonSerde = purchaseSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        // Prepare stream from accepted-purchases
        KStream<String, Purchase> purchaseStream = streamsBuilder.stream("purchases", Consumed.with(STRING_SERDE, purchaseJsonSerde));
        purchaseStream.foreach((cardNo, purchase) -> logger.info("Received accepted purchase: {}", purchase));

        // Convert purchases to balance adjustments
        KStream<String, BalanceAdjustment> balanceAdjustmentStream = purchaseStream.mapValues(purchase -> {
            BalanceAdjustment balanceAdjustment = new BalanceAdjustment();
            balanceAdjustment.setCardNo(purchase.getCardNo());
            balanceAdjustment.setAmount(purchase.getAmount());
            balanceAdjustment.setTimestamp(purchase.getTimestamp());
            return balanceAdjustment;
        });

        balanceAdjustmentStream.to(balanceAdjustmentTopicName, Produced.with(STRING_SERDE, new JsonSerde<>(BalanceAdjustment.class)));
    }

}
