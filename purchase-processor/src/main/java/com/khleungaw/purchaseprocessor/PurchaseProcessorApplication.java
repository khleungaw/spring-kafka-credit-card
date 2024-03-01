package com.khleungaw.purchaseprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PurchaseProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PurchaseProcessorApplication.class, args);
	}

}
