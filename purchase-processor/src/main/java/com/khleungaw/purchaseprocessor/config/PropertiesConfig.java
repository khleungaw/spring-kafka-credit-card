package com.khleungaw.purchaseprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix="purchase-processor")
@ConfigurationPropertiesScan
public class PropertiesConfig {

    private String bootstrapAddress;
    private String acceptedPurchaseTopicName;
    private String balanceTopicName;
    private String limitTopicName;
    private String rejectedPurchaseTopicName;
    private String purchaseTopicName;

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

    public String getBalanceTopicName() {
        return balanceTopicName;
    }

    public void setBalanceTopicName(String balanceTopicName) {
        this.balanceTopicName = balanceTopicName;
    }

    public String getLimitTopicName() {
        return limitTopicName;
    }

    public void setLimitTopicName(String limitTopicName) {
        this.limitTopicName = limitTopicName;
    }

    public String getRejectedPurchaseTopicName() {
        return rejectedPurchaseTopicName;
    }

    public void setRejectedPurchaseTopicName(String rejectedPurchaseTopicName) {
        this.rejectedPurchaseTopicName = rejectedPurchaseTopicName;
    }

    public String getPurchaseTopicName() {
        return purchaseTopicName;
    }

    public void setPurchaseTopicName(String purchaseTopicName) {
        this.purchaseTopicName = purchaseTopicName;
    }

}
