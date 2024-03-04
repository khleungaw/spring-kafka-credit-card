package com.khleungaw.creditcardqueryservice.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class PurchaseWithStatus {

	private UUID id;
	private String cardNo;
	private BigDecimal amount;
	private Date timestamp;
	private PurchaseStatus status;

	public PurchaseWithStatus() {}

	public PurchaseWithStatus(Purchase purchase, PurchaseStatus status) {
		this.id = purchase.getId();
		this.cardNo = purchase.getCardNo();
		this.amount = purchase.getAmount();
		this.timestamp = purchase.getTimestamp();
		this.status = status;
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

	public PurchaseStatus getStatus() {
		return status;
	}

	public void setStatus(PurchaseStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format(
				"{\"id\"=\"%s\",\"cardNo\"=\"%s\",\"amount\"=\"%s\",\"timestamp\"=\"%s\",\"status\"=\"%s\"}",
				id, cardNo, amount, timestamp, status
		);
	}

}
