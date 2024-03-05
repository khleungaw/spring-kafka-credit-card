package com.khleungaw.creditcardqueryservice.config;

import com.khleungaw.creditcardqueryservice.handler.CardInfoHandler;
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
	public RouterFunction<ServerResponse> routes(CardInfoHandler cardInfoHandler, CardPurchaseHandler cardPurchaseHandler) {
		return route(GET("/card"), cardInfoHandler::getAllCardInfo)
			.andRoute(GET("/card/purchase"), cardPurchaseHandler::getAllCardPurchases)
			.andRoute(GET("/card/{cardNo}"), cardInfoHandler::getCardInfo)
			.andRoute(GET("/card/{cardNo}/balance"), cardInfoHandler::getBalance)
			.andRoute(GET("/card/{cardNo}/purchase"), cardPurchaseHandler::getCardPurchases)
			.andRoute(GET("/card/{cardNo}/limit"), cardInfoHandler::getLimit);
	}

}
