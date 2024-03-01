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

    @Value(value = "${acceptedPurchaseTopicName}")
    private String acceptedPurchaseTopicName;

    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();
    private final Logger logger;
    private final JsonSerde<Purchase> purchaseSerde;
    private final JsonSerde<BalanceAdjustment> balanceAdjustmentSerde;

    public AcceptedPurchaseProcessor(JsonSerde<Purchase> purchaseSerde, JsonSerde<BalanceAdjustment> balanceAdjustmentSerde) {
        this.logger = LogManager.getLogger();
        this.purchaseSerde = purchaseSerde;
        this.balanceAdjustmentSerde = balanceAdjustmentSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        // Prepare stream from accepted-purchases
        KStream<String, Purchase> purchaseStream = streamsBuilder.stream(acceptedPurchaseTopicName, Consumed.with(STRING_SERDE, purchaseSerde));

        // Convert purchases to balance adjustments
        purchaseStream.peek((cardNo, purchase)->logger.info("Received accepted purchase: {}", purchase))
        .mapValues(purchase -> {
            BalanceAdjustment balanceAdjustment = new BalanceAdjustment();
            balanceAdjustment.setCardNo(purchase.getCardNo());
            balanceAdjustment.setAmount(purchase.getAmount());
            balanceAdjustment.setTimestamp(purchase.getTimestamp());
            return balanceAdjustment;
        })
        .to(balanceAdjustmentTopicName, Produced.with(STRING_SERDE, balanceAdjustmentSerde));
    }

}
