package com.khleungaw.creditcardqueryservice.config;

import com.khleungaw.creditcardqueryservice.handler.BalanceHandler;
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
	public RouterFunction<ServerResponse> routes(BalanceHandler balanceHandler) {
		return route(GET("/balances/{cardNo}"), balanceHandler::getBalance)
			.andRoute(GET("/balances"), balanceHandler::getAllBalances);
	}

}
