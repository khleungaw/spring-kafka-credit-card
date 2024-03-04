package com.khleungaw.creditcardqueryservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "credit-card-query-service")
@ConfigurationPropertiesScan
public class PropertiesConfig {

	private String bootstrapAddress;
	private String balanceStoreName;
	private String limitStoreName;
	private String purchaseWithStatusStoreName;
	private String acceptedPurchaseTopicName;
	private String balanceTopicName;
	private String balanceAdjustmentTopicName;
	private String limitTopicName;
	private String rejectedPurchaseTopicName;
	private String purchaseTopicName;

	public String getBootstrapAddress() {
		return bootstrapAddress;
	}

	public void setBootstrapAddress(String bootstrapAddress) {
		this.bootstrapAddress = bootstrapAddress;
	}


	public String getBalanceStoreName() {
		return balanceStoreName;
	}

	public void setBalanceStoreName(String balanceStoreName) {
		this.balanceStoreName = balanceStoreName;
	}


	public String getLimitStoreName() {
		return limitStoreName;
	}

	public void setLimitStoreName(String limitStoreName) {
		this.limitStoreName = limitStoreName;
	}


	public String getPurchaseWithStatusStoreName() {
		return purchaseWithStatusStoreName;
	}

	public void setPurchaseWithStatusStoreName(String purchaseWithStatusStoreName) {
		this.purchaseWithStatusStoreName = purchaseWithStatusStoreName;
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

	public String getBalanceAdjustmentTopicName() {
		return balanceAdjustmentTopicName;
	}

	public void setBalanceAdjustmentTopicName(String balanceAdjustmentTopicName) {
		this.balanceAdjustmentTopicName = balanceAdjustmentTopicName;
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
