package com.khleungaw.creditcardproducer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "credit-card-producer")
@ConfigurationPropertiesScan
public class PropertiesConfig {

    private String cardNoStoreName;
    private String balanceTopicName;
    private String limitTopicName;

    public String getCardNoStoreName() {
        return cardNoStoreName;
    }

    public void setCardNoStoreName(String cardNoStoreName) {
        this.cardNoStoreName = cardNoStoreName;
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

}
