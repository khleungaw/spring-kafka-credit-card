package com.khleungaw.creditcardqueryservice;

import com.khleungaw.creditcardqueryservice.handler.BalanceHandler;
import com.khleungaw.creditcardqueryservice.service.BalanceService;
import org.junit.jupiter.api.BeforeAll;

public class BalanceQueryTests {

	BalanceService balanceService;
	BalanceHandler balanceHandler;

	@BeforeAll
	void setUp() {
		balanceHandler = new BalanceHandler(null);
	}

}
