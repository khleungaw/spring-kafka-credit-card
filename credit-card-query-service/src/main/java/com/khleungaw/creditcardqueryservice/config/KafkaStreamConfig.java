package com.khleungaw.creditcardqueryservice.config;

import com.khleungaw.creditcardqueryservice.model.BalanceAdjustment;
import com.khleungaw.creditcardqueryservice.model.Purchase;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.EXACTLY_ONCE_V2;
import static org.apache.kafka.streams.StreamsConfig.PROCESSING_GUARANTEE_CONFIG;

@Configuration
public class KafkaStreamConfig {

    private final PropertiesConfig propertiesConfig;

    public KafkaStreamConfig(PropertiesConfig propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        return new KafkaStreamsConfiguration(Map.of(
            APPLICATION_ID_CONFIG, "credit-card-query-service",
            BOOTSTRAP_SERVERS_CONFIG, propertiesConfig.getBootstrapAddress(),
            DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class,
            DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class,
            PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2
        ));
    }

    @Bean
    public KafkaStreams kafkaStreams() {
        Serdes.StringSerde stringSerde = new Serdes.StringSerde();
        JsonSerde<Purchase> purchaseJsonSerde = new JsonSerde<>(Purchase.class);
        JsonSerde<BigDecimal> bigDecimalJsonSerde = new JsonSerde<>(BigDecimal.class);
        JsonSerde<BalanceAdjustment> balanceAdjustmentJsonSerde = new JsonSerde<>(BalanceAdjustment.class);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        streamsBuilder.globalTable(propertiesConfig.getAcceptedPurchaseTopicName(), Consumed.with(stringSerde, purchaseJsonSerde), Materialized.as(propertiesConfig.getAcceptedPurchaseStoreName()));
        streamsBuilder.globalTable(propertiesConfig.getBalanceTopicName(), Consumed.with(stringSerde, bigDecimalJsonSerde), Materialized.as(propertiesConfig.getBalanceStoreName()));
        streamsBuilder.globalTable(propertiesConfig.getBalanceAdjustmentTopicName(), Consumed.with(stringSerde, balanceAdjustmentJsonSerde), Materialized.as(propertiesConfig.getBalanceAdjustmentStoreName()));
        streamsBuilder.globalTable(propertiesConfig.getLimitTopicName(), Consumed.with(stringSerde, bigDecimalJsonSerde), Materialized.as(propertiesConfig.getLimitStoreName()));
        streamsBuilder.globalTable(propertiesConfig.getRejectedPurchaseTopicName(), Consumed.with(stringSerde, purchaseJsonSerde), Materialized.as(propertiesConfig.getRejectedPurchaseStoreName()));
        streamsBuilder.globalTable(propertiesConfig.getPurchaseTopicName(), Consumed.with(stringSerde, purchaseJsonSerde), Materialized.as(propertiesConfig.getPurchaseStoreName()));
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), kStreamsConfig().asProperties());
        kafkaStreams.start();
        return kafkaStreams;
    }

}
