package com.khleungaw.creditcardproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CreditCardProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardProducerApplication.class, args);
	}

}
