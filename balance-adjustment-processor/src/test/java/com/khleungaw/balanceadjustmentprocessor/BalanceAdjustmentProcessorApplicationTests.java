package com.khleungaw.balanceadjustmentprocessor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(
		topics = {"accepted-purchases", "balances", "limits", "rejected-purchases", "purchases"},
		brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class BalanceAdjustmentProcessorApplicationTests {

	@Test
	void contextLoads() {
	}

}
