package com.khleungaw.creditcardqueryservice.model;

import java.math.BigDecimal;

public class CardInfo {

	private BigDecimal limit;
	private BigDecimal balance;

	public CardInfo() {}

	public CardInfo(BigDecimal limit, BigDecimal balance) {
		this.limit = limit;
		this.balance = balance;
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
			"\"limit\"=\"%s\",\"balance\"=\"%s\"",
			limit, balance
		);
	}

}
