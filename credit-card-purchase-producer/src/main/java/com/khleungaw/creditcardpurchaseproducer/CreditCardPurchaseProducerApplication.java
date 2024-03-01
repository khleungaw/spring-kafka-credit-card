package com.khleungaw.creditcardpurchaseproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CreditCardPurchaseProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardPurchaseProducerApplication.class, args);
	}

}
