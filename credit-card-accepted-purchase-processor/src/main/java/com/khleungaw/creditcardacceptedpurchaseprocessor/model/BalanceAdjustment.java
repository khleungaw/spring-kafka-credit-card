package com.khleungaw.creditcardacceptedpurchaseprocessor.model;

import java.math.BigDecimal;
import java.util.Date;

public class BalanceAdjustment {

    private String cardNo;
    private BigDecimal amount;
    private Date timestamp;
    private BalanceAdjustmentType type;

    public BalanceAdjustment() {}

    public BalanceAdjustment(Purchase purchase) {
        this.cardNo = purchase.getCardNo();
        this.amount = purchase.getAmount();
        this.timestamp = purchase.getTimestamp();
        this.type = BalanceAdjustmentType.PURCHASE;
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

    public BalanceAdjustmentType getType() {
        return type;
    }

    public void setType(BalanceAdjustmentType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format(
                "{\"cardNo\":\"%s\",\"amount\":%s,\"timestamp\": \"%s\", \"type\": \"%s\"}",
                cardNo, amount, timestamp, type
        );
    }

}
