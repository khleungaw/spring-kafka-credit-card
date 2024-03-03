package com.khleungaw.creditcardproducer.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

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
			APPLICATION_ID_CONFIG, "credit-card-card-producer",
			BOOTSTRAP_SERVERS_CONFIG, propertiesConfig.getBootstrapAddress(),
			DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class,
			DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class,
			PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE_V2
		));
	}

	@Bean
	public KafkaStreams kafkaStreams() {
		StreamsBuilder streamsBuilder = new StreamsBuilder();
		streamsBuilder.globalTable(propertiesConfig.getLimitTopicName(), Materialized.as(propertiesConfig.getCardNoStoreName()));
		KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), kStreamsConfig().asProperties());
		kafkaStreams.start();
		return kafkaStreams;
	}

}