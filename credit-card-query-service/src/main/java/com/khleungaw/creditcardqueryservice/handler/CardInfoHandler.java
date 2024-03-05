package com.khleungaw.creditcardqueryservice.handler;

import com.khleungaw.creditcardqueryservice.model.CardInfo;
import com.khleungaw.creditcardqueryservice.service.CardInfoService;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CardInfoHandler {

	private final CardInfoService cardInfoService;

	public CardInfoHandler(CardInfoService cardInfoService) {
		this.cardInfoService = cardInfoService;
	}

	public Mono<ServerResponse> getBalance(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<KeyValue<String, BigDecimal>> resultMono = Mono.just(cardInfoService.getBalance(cardNo));
		return resultMono.flatMap(result ->
			result.value != null ? ServerResponse.ok().bodyValue(result) : ServerResponse.status(404).build()
		);
	}

	public Mono<ServerResponse> getAllBalances(ServerRequest ignoredReq) {
		Mono<Map<String, BigDecimal>> resultMono = Mono.just(cardInfoService.getAllBalances());
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result));
	}

	public Mono<ServerResponse> getLimit(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<KeyValue<String, BigDecimal>> resultMono = Mono.just(cardInfoService.getLimit(cardNo));
		return resultMono.flatMap(result ->
			result.value != null ? ServerResponse.ok().bodyValue(result) : ServerResponse.status(404).build()
		);
	}

	public Mono<ServerResponse> getAllLimits(ServerRequest ignoredReq) {
		Mono<Map<String, BigDecimal>> resultMono = Mono.just(cardInfoService.getAllLimits());
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result));
	}

	public Mono<ServerResponse> getCardInfo(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<KeyValue<String, CardInfo>> resultMono = Mono.just(cardInfoService.getCardInfo(cardNo));
		return resultMono.flatMap(result ->
			result.value != null ? ServerResponse.ok().bodyValue(result) : ServerResponse.status(404).build()
		);
	}

	public Mono<ServerResponse> getAllCardInfo(ServerRequest ignoredReq) {
		Mono<Map<String, CardInfo>> resultMono = Mono.just(cardInfoService.getAllCardInfo());
		return resultMono.flatMap(result -> ServerResponse.ok().bodyValue(result));
	}

}
