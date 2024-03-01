package com.khleungaw.purchaseproducer.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


public class Purchase {

    private UUID id;
    private String cardNo;
    private BigDecimal amount;
    private Date timestamp;

    public Purchase() {}

    public Purchase(PurchaseDTO dto) {
        this.id = UUID.randomUUID();
        this.cardNo = dto.getCardNo();
        this.amount = new BigDecimal(dto.getAmount());
        this.timestamp = new Date();
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
