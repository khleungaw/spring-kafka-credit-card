package com.khleungaw.balanceadjustmentprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "balance-adjustment-processor")
@ConfigurationPropertiesScan
public class PropertiesConfig {

    private String balanceStoreName;
    private String balanceGlobalStoreName;
    private String balanceTopicName;
    private String balanceAdjustmentTopicName;

    public String getBalanceStoreName() {
        return balanceStoreName;
    }

    public void setBalanceStoreName(String balanceStoreName) {
        this.balanceStoreName = balanceStoreName;
    }

    public String getBalanceTopicName() {
        return balanceTopicName;
    }

    public void setBalanceTopicName(String balanceTopicName) {
        this.balanceTopicName = balanceTopicName;
    }

    public String getBalanceGlobalStoreName() {
        return balanceGlobalStoreName;
    }

    public void setBalanceGlobalStoreName(String balanceGlobalStoreName) {
        this.balanceGlobalStoreName = balanceGlobalStoreName;
    }

    public String getBalanceAdjustmentTopicName() {
        return balanceAdjustmentTopicName;
    }

    public void setBalanceAdjustmentTopicName(String balanceAdjustmentTopicName) {
        this.balanceAdjustmentTopicName = balanceAdjustmentTopicName;
    }

}
