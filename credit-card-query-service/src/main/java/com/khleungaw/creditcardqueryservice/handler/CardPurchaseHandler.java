package com.khleungaw.creditcardqueryservice.handler;

import com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus;
import com.khleungaw.creditcardqueryservice.service.CardPurchaseService;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CardPurchaseHandler {

	private final CardPurchaseService cardPurchaseService;

	public CardPurchaseHandler(CardPurchaseService cardPurchaseService) {
		this.cardPurchaseService = cardPurchaseService;
	}

	public Mono<ServerResponse> getCardPurchases(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		List<PurchaseWithStatus> result = cardPurchaseService.getCardPurchases(cardNo);
		if (result != null && !result.isEmpty()) {
			return ServerResponse.ok().bodyValue(cardPurchaseService.getCardPurchases(cardNo));
		} else {
			return ServerResponse.notFound().build();
		}
	}

	public Mono<ServerResponse> getAllCardPurchases(ServerRequest ignoredReq) {
		List<KeyValue<String, List<PurchaseWithStatus>>> result = cardPurchaseService.getAllCardPurchases();
		if (result != null && !result.isEmpty()) {
			return ServerResponse.ok().bodyValue(result);
		} else {
			return ServerResponse.notFound().build();
		}
	}

}
