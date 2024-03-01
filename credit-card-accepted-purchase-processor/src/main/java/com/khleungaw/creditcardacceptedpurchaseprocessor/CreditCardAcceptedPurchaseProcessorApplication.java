package com.khleungaw.creditcardacceptedpurchaseprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CreditCardAcceptedPurchaseProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardAcceptedPurchaseProcessorApplication.class, args);
	}

}
