package com.khleungaw.creditcardpurchaseprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CreditCardPurchaseProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardPurchaseProcessorApplication.class, args);
	}

}
