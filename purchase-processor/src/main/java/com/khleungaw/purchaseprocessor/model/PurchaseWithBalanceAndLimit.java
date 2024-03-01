package com.khleungaw.purchaseprocessor.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class PurchaseWithBalanceAndLimit {

	private UUID id;
	private String cardNo;
	private BigDecimal amount;
	private Date timestamp;
	private BigDecimal balanceAmount;
	private BigDecimal limitAmount;

	public PurchaseWithBalanceAndLimit() {}

	public PurchaseWithBalanceAndLimit(PurchaseWithBalance purchaseWithBalance, String limit) {
		this.id = purchaseWithBalance.getId();
		this.cardNo = purchaseWithBalance.getCardNo();
		this.amount = purchaseWithBalance.getAmount();
		this.timestamp = purchaseWithBalance.getTimestamp();
		this.balanceAmount = purchaseWithBalance.getBalanceAmount();
		this.limitAmount = limit == null ? null : new BigDecimal(limit);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	@Override
	public String toString() {
		return String.format(
				"{\"cardNo\":\"%s\",\"amount\":%s,\"timestamp\": \"%s\",\"balanceAmount\":%s, \"limitAmount\":%s}",
				cardNo, amount, timestamp, balanceAmount, limitAmount
		);
	}

}
