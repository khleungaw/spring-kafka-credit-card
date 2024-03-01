package com.khleungaw.creditcardacceptedpurchaseprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "credit-card")
@ConfigurationPropertiesScan
public class PropertiesConfig {

    private String acceptedPurchaseTopicName;
    private String balanceAdjustmentTopicName;

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
