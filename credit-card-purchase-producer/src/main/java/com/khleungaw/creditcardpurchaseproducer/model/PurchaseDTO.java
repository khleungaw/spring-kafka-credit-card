package com.khleungaw.creditcardpurchaseproducer.model;

public class PurchaseDTO {

    private String cardNo;
    private String amount;

    public PurchaseDTO() {}

    public PurchaseDTO(String cardNo, String amount) {
        this.cardNo = cardNo;
        this.amount = amount;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format(
                "{\"cardNo\":\"%s\",\"amount\":%s}",
                cardNo, amount
        );
    }

}
