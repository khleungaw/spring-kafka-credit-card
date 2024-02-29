package com.khleungaw.creditcardbalanceadjustmentprocessor.config;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        return new KafkaStreamsConfiguration(Map.of(
            APPLICATION_ID_CONFIG, "credit-card-balance-adjustment-processor",
            BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
            DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class,
            DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class,
            PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2
        ));
    }

}
