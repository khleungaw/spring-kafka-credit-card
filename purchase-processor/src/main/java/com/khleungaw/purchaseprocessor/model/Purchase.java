package com.khleungaw.purchaseprocessor.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Purchase {

    private UUID id;
    private String cardNo;
    private BigDecimal amount;
    private Date timestamp;

    public Purchase() {}

    public Purchase(PurchaseWithBalanceAndLimit purchaseWithBalanceAndLimit) {
        this.id = purchaseWithBalanceAndLimit.getId();
        this.cardNo = purchaseWithBalanceAndLimit.getCardNo();
        this.amount = purchaseWithBalanceAndLimit.getAmount();
        this.timestamp = purchaseWithBalanceAndLimit.getTimestamp();
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

    @Override
    public String toString() {
        return String.format(
                "{\"id\"=\"%s\",\"cardNo\"=\"%s\",\"amount\"=\"%s\",\"timestamp\"=\"%s\"}",
                id, cardNo, amount, timestamp
        );
    }

}
