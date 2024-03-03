package com.khleungaw.creditcardqueryservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(
		topics = {"accepted-purchases", "balances", "balance-adjustments", "limits", "rejected-purchases", "purchases"},
		brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class CreditCardQueryServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
