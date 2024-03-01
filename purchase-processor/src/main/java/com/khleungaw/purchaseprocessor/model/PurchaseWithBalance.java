package com.khleungaw.purchaseprocessor.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class PurchaseWithBalance {

	private UUID id;
	private String cardNo;
	private BigDecimal amount;
	private Date timestamp;
	private BigDecimal balanceAmount;

	public PurchaseWithBalance() {}

	public PurchaseWithBalance(Purchase purchase, String balance) {
		this.id = purchase.getId();
		this.cardNo = purchase.getCardNo();
		this.amount = purchase.getAmount();
		this.timestamp = purchase.getTimestamp();
		this.balanceAmount = balance == null ? null : new BigDecimal(balance);
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

	@Override
	public String toString() {
		return String.format(
				"{\"cardNo\":\"%s\",\"amount\":%s,\"timestamp\": \"%s\",\"balanceAmount\":%s}",
				cardNo, amount, timestamp, balanceAmount
		);
	}

}
