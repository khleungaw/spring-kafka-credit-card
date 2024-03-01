package com.khleungaw.balanceadjustmentprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BalanceAdjustmentProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalanceAdjustmentProcessorApplication.class, args);
	}

}
