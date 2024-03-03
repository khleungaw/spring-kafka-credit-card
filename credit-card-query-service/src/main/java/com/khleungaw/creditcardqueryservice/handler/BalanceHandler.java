package com.khleungaw.creditcardqueryservice.handler;

import com.khleungaw.creditcardqueryservice.service.BalanceService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class BalanceHandler {

	private final BalanceService balanceService;

	public BalanceHandler(BalanceService balanceService) {
		this.balanceService = balanceService;
	}

	public Mono<ServerResponse> getBalance(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		BigDecimal result = balanceService.getBalance(cardNo);
		if (result != null) {
			return ServerResponse.ok().bodyValue(balanceService.getBalance(cardNo));
		} else {
			return ServerResponse.notFound().build();
		}
	}

	public Mono<ServerResponse> getAllBalances(ServerRequest ignoredReq) {
		return ServerResponse.ok().bodyValue(balanceService.getAllBalances());
	}
}
