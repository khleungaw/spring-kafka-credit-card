package com.khleungaw.creditcardpurchaseproducer.config;

import com.khleungaw.creditcardpurchaseproducer.PurchaseHandler;
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
	public RouterFunction<ServerResponse> routes(PurchaseHandler purchaseHandler) {
		return route(POST("/purchases"), purchaseHandler::create);
	}

}
