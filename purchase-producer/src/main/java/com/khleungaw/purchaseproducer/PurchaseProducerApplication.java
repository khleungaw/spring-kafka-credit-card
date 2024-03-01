package com.khleungaw.purchaseproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PurchaseProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PurchaseProducerApplication.class, args);
	}

}
