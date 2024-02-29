package com.khleungaw.creditcardbalanceadjustmentprocessor;

import com.khleungaw.creditcardbalanceadjustmentprocessor.model.BalanceAdjustment;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceCalculator implements Processor<String, BalanceAdjustment, String, BigDecimal> {

    @Value(value = "${balanceStoreName}")
    private String balanceStoreName;

    private KeyValueStore<String, BigDecimal> balanceStore;
    private ProcessorContext<String, BigDecimal> context;

    @Override
    public void init(ProcessorContext<String, BigDecimal> context) {
        this.balanceStore = context.getStateStore(balanceStoreName);
        this.context = context;
    }

    @Override
    public void process(Record<String, BalanceAdjustment> record) {
        BalanceAdjustment balanceAdjustment = record.value();
        String cardNo = record.key();
        BigDecimal currentBalance = balanceStore.get(cardNo);

        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        BigDecimal newBalance = currentBalance.add(balanceAdjustment.getAmount());
        balanceStore.put(cardNo, newBalance);
        context.forward(record.withKey(cardNo).withValue(newBalance));
    }

}
