package com.khleungaw.balanceadjustmentprocessor;

import com.khleungaw.balanceadjustmentprocessor.config.PropertiesConfig;
import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceAdjustmentProcessor {

    private final Logger logger;
    private final PropertiesConfig propertiesConfig;
    private final JsonSerde<BalanceAdjustment> balanceAdjustmentSerde;
    private final JsonSerde<BigDecimal> bigDecimalSerde;
    private static final Serdes.StringSerde STRING_SERDE = new Serdes.StringSerde();

    public BalanceAdjustmentProcessor(PropertiesConfig propertiesConfig, JsonSerde<BalanceAdjustment> balanceAdjustmentSerde, JsonSerde<BigDecimal> bigDecimalSerde) {
        this.logger = LogManager.getLogger();
        this.propertiesConfig = propertiesConfig;
        this.balanceAdjustmentSerde = balanceAdjustmentSerde;
        this.bigDecimalSerde = bigDecimalSerde;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder.globalTable(propertiesConfig.getBalanceTopicName(), Consumed.with(STRING_SERDE, bigDecimalSerde),
            Materialized.<String, BigDecimal, KeyValueStore<Bytes, byte[]>> as(propertiesConfig.getBalanceStoreName())
                .withKeySerde(STRING_SERDE)
                .withValueSerde(bigDecimalSerde)
        );

        ProcessorSupplier<String, BalanceAdjustment, String, BigDecimal> balanceCalculatorSupplier = () -> new BalanceCalculator(propertiesConfig.getBalanceStoreName());
        KStream<String, BalanceAdjustment> balanceAdjustmentStream = streamsBuilder.stream(propertiesConfig.getBalanceAdjustmentTopicName(), Consumed.with(STRING_SERDE, balanceAdjustmentSerde));
        balanceAdjustmentStream.peek((cardNo, balanceAdjustment) -> logger.info("Received balance adjustment: {} for card: {}", balanceAdjustment, cardNo))
            .process(balanceCalculatorSupplier, Named.as("balance-calculator"))
            .peek((cardNo, newBalance) -> logger.info("Stored balance for card {} : {}", cardNo, newBalance))
            .to(propertiesConfig.getBalanceTopicName(), Produced.with(STRING_SERDE, bigDecimalSerde));
    }

}
