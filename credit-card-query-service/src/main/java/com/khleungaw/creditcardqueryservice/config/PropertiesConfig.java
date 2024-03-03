package com.khleungaw.creditcardqueryservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "credit-card-query-service")
@ConfigurationPropertiesScan
public class PropertiesConfig {

	private String bootstrapAddress;
	private String acceptedPurchaseStoreName;
	private String balanceStoreName;
	private String balanceAdjustmentStoreName;
	private String limitStoreName;
	private String rejectedPurchaseStoreName;
	private String purchaseStoreName;
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

	public String getAcceptedPurchaseStoreName() {
		return acceptedPurchaseStoreName;
	}

	public void setAcceptedPurchaseStoreName(String acceptedPurchaseStoreName) {
		this.acceptedPurchaseStoreName = acceptedPurchaseStoreName;
	}

	public String getBalanceStoreName() {
		return balanceStoreName;
	}

	public void setBalanceStoreName(String balanceStoreName) {
		this.balanceStoreName = balanceStoreName;
	}

	public String getBalanceAdjustmentStoreName() {
		return balanceAdjustmentStoreName;
	}

	public void setBalanceAdjustmentStoreName(String balanceAdjustmentStoreName) {
		this.balanceAdjustmentStoreName = balanceAdjustmentStoreName;
	}

	public String getLimitStoreName() {
		return limitStoreName;
	}

	public void setLimitStoreName(String limitStoreName) {
		this.limitStoreName = limitStoreName;
	}

	public String getRejectedPurchaseStoreName() {
		return rejectedPurchaseStoreName;
	}

	public void setRejectedPurchaseStoreName(String rejectedPurchaseStoreName) {
		this.rejectedPurchaseStoreName = rejectedPurchaseStoreName;
	}

	public String getPurchaseStoreName() {
		return purchaseStoreName;
	}

	public void setPurchaseStoreName(String purchaseStoreName) {
		this.purchaseStoreName = purchaseStoreName;
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
