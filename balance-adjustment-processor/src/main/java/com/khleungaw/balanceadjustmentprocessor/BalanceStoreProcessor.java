package com.khleungaw.balanceadjustmentprocessor;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

import java.math.BigDecimal;

public class BalanceStoreProcessor implements Processor<String, BigDecimal, Void, Void> {

	private final String balanceStoreName;
	private KeyValueStore<String, BigDecimal> balanceStore;

	public BalanceStoreProcessor(String balanceGlobalStoreName) {
		this.balanceStoreName = balanceGlobalStoreName;
	}

	@Override
	public void init(ProcessorContext<Void, Void> context) {
		balanceStore = context.getStateStore(balanceStoreName);
	}

	@Override
	public void process(Record<String, BigDecimal> record) {
		balanceStore.put(record.key(), record.value());
	}

}
