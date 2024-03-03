package com.khleungaw.acceptedpurchaseprocessor;

import com.khleungaw.acceptedpurchaseprocessor.config.PropertiesConfig;
import com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustment;
import com.khleungaw.acceptedpurchaseprocessor.model.BalanceAdjustmentType;
import com.khleungaw.acceptedpurchaseprocessor.model.Purchase;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class AcceptedPurchaseProcessor {

    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();

    private final Logger logger;
    private final String acceptedPurchaseTopicName;
    private final String balanceAdjustmentTopicName;
    private final JsonSerde<Purchase> purchaseSerde;
    private final JsonSerde<BalanceAdjustment> balanceAdjustmentSerde;

    public AcceptedPurchaseProcessor(PropertiesConfig propertiesConfig, JsonSerde<Purchase> purchaseSerde, JsonSerde<BalanceAdjustment> balanceAdjustmentSerde) {
        this.logger = LogManager.getLogger();
        this.acceptedPurchaseTopicName = propertiesConfig.getAcceptedPurchaseTopicName();
        this.balanceAdjustmentTopicName = propertiesConfig.getBalanceAdjustmentTopicName();
        this.balanceAdjustmentSerde = balanceAdjustmentSerde;
        this.purchaseSerde = purchaseSerde;
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
            balanceAdjustment.setType(BalanceAdjustmentType.PURCHASE);
            return balanceAdjustment;
        })
        .to(balanceAdjustmentTopicName, Produced.with(STRING_SERDE, balanceAdjustmentSerde));
    }

}
