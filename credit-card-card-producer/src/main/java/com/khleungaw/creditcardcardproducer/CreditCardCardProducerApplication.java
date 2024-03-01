package com.khleungaw.creditcardcardproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CreditCardCardProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardCardProducerApplication.class, args);
	}

}
