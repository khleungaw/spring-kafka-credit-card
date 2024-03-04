package com.khleungaw.balanceadjustmentprocessor;

import com.khleungaw.balanceadjustmentprocessor.model.BalanceAdjustment;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import java.math.BigDecimal;

public class BalanceCalculator implements Processor<String, BalanceAdjustment, String, BigDecimal> {

    private final String balanceStoreName;
    private ReadOnlyKeyValueStore<String, ValueAndTimestamp<BigDecimal>> balanceStore;
    private ProcessorContext<String, BigDecimal> context;

    public BalanceCalculator(String balanceStoreName) {
        this.balanceStoreName = balanceStoreName;
    }

    @Override
    public void init(ProcessorContext<String, BigDecimal> context) {
        this.balanceStore = context.getStateStore(balanceStoreName);
        this.context = context;
    }

    @Override
    public void process(Record<String, BalanceAdjustment> record) {
        BalanceAdjustment balanceAdjustment = record.value();
        String cardNo = record.key();
        BigDecimal currentBalance = balanceStore.get(cardNo).value();

        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        BigDecimal newBalance = currentBalance.add(balanceAdjustment.getAmount());
        context.forward(record.withKey(cardNo).withValue(newBalance));
    }

}
