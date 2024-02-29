package com.khleungaw.creditcardbalanceadjustmentprocessor;

import com.khleungaw.creditcardbalanceadjustmentprocessor.model.BalanceAdjustment;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceAdjustmentProcessor {

    @Value(value = "${balanceTopicName}")
    private String balanceTopicName;

    @Value(value = "${balanceAdjustmentTopicName}")
    private String balanceAdjustmentTopicName;

    @Value(value = "${balanceStoreName}")
    private String balanceStoreName;

    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();
    private final Logger logger;
    private final JsonSerde<BalanceAdjustment> balanceAdjustmentSerde;
    private final JsonSerde<BigDecimal> bigDecimalSerde;

    public BalanceAdjustmentProcessor(JsonSerde<BalanceAdjustment> balanceAdjustmentSerde, JsonSerde<BigDecimal> bigDecimalSerde) {
        this.logger = LogManager.getLogger();
        this.balanceAdjustmentSerde = balanceAdjustmentSerde;
        this.bigDecimalSerde = bigDecimalSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, BalanceAdjustment> balanceAdjustmentStream = streamsBuilder.stream(balanceAdjustmentTopicName, Consumed.with(STRING_SERDE, balanceAdjustmentSerde));
        StoreBuilder<KeyValueStore<String, BigDecimal>> balanceStoreBuilder = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(balanceStoreName),
            STRING_SERDE,
            bigDecimalSerde
        );
        streamsBuilder.addStateStore(balanceStoreBuilder);

        balanceAdjustmentStream.peek((cardNo, balanceAdjustment) -> logger.info("Received balance adjustment: {} for card: {}", balanceAdjustment, cardNo))
            .process(BalanceCalculator::new, Named.as("balance-calculator"), balanceStoreName)
            .peek((cardNo, newBalance) -> logger.info("Stored balance for card {} : {}", cardNo, newBalance))
            .to(balanceTopicName, Produced.with(STRING_SERDE, bigDecimalSerde));
    }

}
