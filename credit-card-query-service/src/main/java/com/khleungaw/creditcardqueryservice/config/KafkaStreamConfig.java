package com.khleungaw.creditcardqueryservice.config;

import com.khleungaw.creditcardqueryservice.model.Purchase;
import com.khleungaw.creditcardqueryservice.model.PurchaseStatus;
import com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private final JsonSerde<Purchase> purchaseSerde;

    public KafkaStreamConfig(PropertiesConfig propertiesConfig, JsonSerde<Purchase> purchaseSerde) {
        this.propertiesConfig = propertiesConfig;
        this.purchaseSerde = purchaseSerde;
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
        // Serdes
        Serdes.StringSerde stringSerde = new Serdes.StringSerde();
        JsonSerde<BigDecimal> bigDecimalJsonSerde = new JsonSerde<>(BigDecimal.class);

        // Streams
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Purchase> acceptedPurchaseStream = streamsBuilder.stream(propertiesConfig.getAcceptedPurchaseTopicName(), Consumed.with(stringSerde, purchaseSerde));
        KStream<String, Purchase> rejectedPurchaseStream = streamsBuilder.stream(propertiesConfig.getRejectedPurchaseTopicName(), Consumed.with(stringSerde, purchaseSerde));
        GlobalKTable<String, BigDecimal> ignoredBalanceTable = streamsBuilder.globalTable(propertiesConfig.getBalanceTopicName(), Consumed.with(stringSerde, bigDecimalJsonSerde), Materialized.as(propertiesConfig.getBalanceStoreName()));
        GlobalKTable<String, BigDecimal> ignoredLimitTable = streamsBuilder.globalTable(propertiesConfig.getLimitTopicName(), Consumed.with(stringSerde, bigDecimalJsonSerde), Materialized.as(propertiesConfig.getLimitStoreName()));

        // Add status to purchase
        KStream<String, PurchaseWithStatus> acceptedPurchaseWithStatusStream = acceptedPurchaseStream
            .mapValues(purchase -> new PurchaseWithStatus(purchase, PurchaseStatus.ACCEPTED));
        KStream<String, PurchaseWithStatus> rejectedPurchaseWithStatusStream = rejectedPurchaseStream
            .mapValues(purchase -> new PurchaseWithStatus(purchase, PurchaseStatus.REJECTED));

        acceptedPurchaseWithStatusStream
            .merge(rejectedPurchaseWithStatusStream)
            .groupByKey()
            .aggregate(
                ArrayList::new,
                (String key, PurchaseWithStatus value, ArrayList<PurchaseWithStatus> list) -> {
                    list.add(value);
                    return list;
                },
                Materialized.<String, ArrayList<PurchaseWithStatus>, KeyValueStore<Bytes, byte[]>>as(propertiesConfig.getPurchaseWithStatusStoreName())
                    .withKeySerde(stringSerde)
                    .withValueSerde(new JsonSerde<>(ArrayList.class))
            );

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), kStreamsConfig().asProperties());
        kafkaStreams.start();
        return kafkaStreams;
    }

}
