package com.khleungaw.purchaseproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(
		topics = {"accepted-purchases", "balances", "limits", "rejected-purchases", "purchases"},
		partitions = 1,
		brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class PurchaseProducerApplicationTests {

	@Test
	void contextLoads() {
	}

}
