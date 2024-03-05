package com.khleungaw.creditcardqueryservice.handler;

import com.khleungaw.creditcardqueryservice.model.CardInfo;
import com.khleungaw.creditcardqueryservice.service.CardInfoService;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class CardInfoHandler {

	private final CardInfoService cardInfoService;

	public CardInfoHandler(CardInfoService cardInfoService) {
		this.cardInfoService = cardInfoService;
	}

	public Mono<ServerResponse> getBalance(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<BigDecimal> resultMono = Mono.justOrEmpty(cardInfoService.getBalance(cardNo));
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result))
			.switchIfEmpty(ServerResponse.status(404).build());
	}

	public Mono<ServerResponse> getLimit(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<BigDecimal> resultMono = Mono.justOrEmpty(cardInfoService.getLimit(cardNo));
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result))
			.switchIfEmpty(ServerResponse.status(404).build());
	}

	public Mono<ServerResponse> getCardInfo(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<CardInfo> resultMono = Mono.justOrEmpty(cardInfoService.getCardInfo(cardNo));
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result))
			.switchIfEmpty(ServerResponse.status(404).build());
	}

	public Mono<ServerResponse> getAllCardInfo(ServerRequest ignoredReq) {
		Mono<List<CardInfo>> resultMono = Mono.just(cardInfoService.getAllCardInfo());
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result));
	}

}
