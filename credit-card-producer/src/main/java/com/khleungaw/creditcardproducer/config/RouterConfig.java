package com.khleungaw.creditcardproducer.config;

import com.khleungaw.creditcardproducer.CreditCardHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(CreditCardHandler creditCardHandler) {
		return route(POST("/credit-cards"), creditCardHandler::create)
			.andRoute(POST("/credit-cards/test"), creditCardHandler::checkUnique);
	}

}
