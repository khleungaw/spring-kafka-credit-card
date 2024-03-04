package com.khleungaw.creditcardqueryservice.config;

import com.khleungaw.creditcardqueryservice.handler.BalanceHandler;
import com.khleungaw.creditcardqueryservice.handler.CardPurchaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(BalanceHandler balanceHandler, CardPurchaseHandler cardPurchaseHandler) {
		return route(GET("/balances/{cardNo}"), balanceHandler::getBalance)
			.andRoute(GET("/balances"), balanceHandler::getAllBalances)
			.andRoute(GET("/purchases/{cardNo}"), cardPurchaseHandler::getCardPurchases)
			.andRoute(GET("/purchases"), cardPurchaseHandler::getAllCardPurchases);
	}

}
