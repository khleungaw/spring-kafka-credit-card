package com.khleungaw.balanceadjustmentprocessor;

import com.khleungaw.balanceadjustmentprocessor.config.PropertiesConfig;
import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceAdjustmentProcessor {

    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();

    private final Logger logger;
    private final String balanceStoreName;
    private final String balanceTopicName;
    private final String balanceAdjustmentTopicName;
    private final JsonSerde<BalanceAdjustment> balanceAdjustmentSerde;
    private final JsonSerde<BigDecimal> bigDecimalSerde;

    public BalanceAdjustmentProcessor(PropertiesConfig propertiesConfig, JsonSerde<BalanceAdjustment> balanceAdjustmentSerde, JsonSerde<BigDecimal> bigDecimalSerde) {
        this.logger = LogManager.getLogger();
        this.balanceStoreName = propertiesConfig.getBalanceStoreName();
        this.balanceTopicName = propertiesConfig.getBalanceTopicName();
        this.balanceAdjustmentTopicName = propertiesConfig.getBalanceAdjustmentTopicName();
        this.balanceAdjustmentSerde = balanceAdjustmentSerde;
        this.bigDecimalSerde = bigDecimalSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        ProcessorSupplier<String, BigDecimal, Void, Void> balanceStoreProcessorSupplier = () -> new BalanceStoreProcessor(balanceStoreName);
        streamsBuilder.addGlobalStore(
            Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore(balanceStoreName), STRING_SERDE, bigDecimalSerde),
            balanceTopicName,
            Consumed.with(STRING_SERDE, bigDecimalSerde),
            balanceStoreProcessorSupplier
        );

        ProcessorSupplier<String, BalanceAdjustment, String, BigDecimal> balanceCalculatorSupplier = () -> new BalanceCalculator(balanceStoreName);
        KStream<String, BalanceAdjustment> balanceAdjustmentStream = streamsBuilder.stream(balanceAdjustmentTopicName, Consumed.with(STRING_SERDE, balanceAdjustmentSerde));
        balanceAdjustmentStream.peek((cardNo, balanceAdjustment) -> logger.info("Received balance adjustment: {} for card: {}", balanceAdjustment, cardNo))
            .process(balanceCalculatorSupplier, Named.as("balance-calculator"))
            .peek((cardNo, newBalance) -> logger.info("Stored balance for card {} : {}", cardNo, newBalance))
            .to(balanceTopicName, Produced.with(STRING_SERDE, bigDecimalSerde));
    }

}
