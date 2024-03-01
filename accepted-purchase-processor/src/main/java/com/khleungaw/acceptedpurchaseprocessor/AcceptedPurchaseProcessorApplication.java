package com.khleungaw.acceptedpurchaseprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AcceptedPurchaseProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcceptedPurchaseProcessorApplication.class, args);
	}

}
