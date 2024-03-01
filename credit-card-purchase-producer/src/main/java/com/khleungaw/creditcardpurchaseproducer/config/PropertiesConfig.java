package com.khleungaw.creditcardpurchaseproducer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix="credit-card")
@ConfigurationPropertiesScan
public class PropertiesConfig {

    private String purchaseTopicName;

    public String getPurchaseTopicName() {
        return purchaseTopicName;
    }

    public void setPurchaseTopicName(String purchaseTopicName) {
        this.purchaseTopicName = purchaseTopicName;
    }

}
