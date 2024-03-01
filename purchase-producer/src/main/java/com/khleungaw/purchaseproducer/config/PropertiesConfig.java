package com.khleungaw.purchaseproducer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix="purchase-producer")
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
