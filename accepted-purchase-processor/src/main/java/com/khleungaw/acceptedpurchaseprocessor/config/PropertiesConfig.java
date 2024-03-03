package com.khleungaw.acceptedpurchaseprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "accepted-purchase-processor")
@ConfigurationPropertiesScan
public class PropertiesConfig {

    private String bootstrapAddress;
    private String acceptedPurchaseTopicName;
    private String balanceAdjustmentTopicName;

    public String getBootstrapAddress() {
        return bootstrapAddress;
    }

    public void setBootstrapAddress(String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }

    public String getAcceptedPurchaseTopicName() {
        return acceptedPurchaseTopicName;
    }

    public void setAcceptedPurchaseTopicName(String acceptedPurchaseTopicName) {
        this.acceptedPurchaseTopicName = acceptedPurchaseTopicName;
    }

    public String getBalanceAdjustmentTopicName() {
        return balanceAdjustmentTopicName;
    }

    public void setBalanceAdjustmentTopicName(String balanceAdjustmentTopicName) {
        this.balanceAdjustmentTopicName = balanceAdjustmentTopicName;
    }

}
