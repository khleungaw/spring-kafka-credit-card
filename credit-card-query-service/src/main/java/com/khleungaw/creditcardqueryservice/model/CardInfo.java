package com.khleungaw.creditcardqueryservice.model;

import java.math.BigDecimal;

public class CardInfo {

	private String cardNo;
	private BigDecimal limit;
	private BigDecimal balance;

	public CardInfo() {}

	public CardInfo(String cardNo, BigDecimal limit, BigDecimal balance) {
		this.cardNo = cardNo;
		this.limit = limit;
		this.balance = balance;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return String.format(
			"\"cardNo\"=\"%s\",\"limit\"=\"%s\",\"balance\"=\"%s\"",
			cardNo, limit, balance
		);
	}

}
