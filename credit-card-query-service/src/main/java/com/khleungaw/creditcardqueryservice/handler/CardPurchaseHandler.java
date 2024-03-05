package com.khleungaw.creditcardqueryservice.handler;

import com.khleungaw.creditcardqueryservice.model.CardInfo;
import com.khleungaw.creditcardqueryservice.model.PurchaseWithStatus;
import com.khleungaw.creditcardqueryservice.service.CardInfoService;
import com.khleungaw.creditcardqueryservice.service.CardPurchaseService;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CardPurchaseHandler {

	private final CardInfoService cardInfoService;
	private final CardPurchaseService cardPurchaseService;

	public CardPurchaseHandler(CardInfoService cardInfoService, CardPurchaseService cardPurchaseService) {
		this.cardInfoService = cardInfoService;
		this.cardPurchaseService = cardPurchaseService;
	}

	public Mono<ServerResponse> getCardPurchases(ServerRequest req) {
		String cardNo = req.pathVariables().get("cardNo");
		Mono<List<PurchaseWithStatus>> resultMono = Mono.justOrEmpty(cardPurchaseService.getCardPurchases(cardNo));
		Mono<CardInfo> cardInfoMono = Mono.justOrEmpty(cardInfoService.getCardInfo(cardNo).value);

		return resultMono
			.flatMap(purchases -> ServerResponse.ok().bodyValue(purchases))
			.switchIfEmpty(cardInfoMono
				.flatMap(cardInfo -> ServerResponse.ok().bodyValue(List.of())
				.switchIfEmpty(ServerResponse.notFound().build())
			)
		);
	}

	public Mono<ServerResponse> getAllCardPurchases(ServerRequest ignoredReq) {
		Mono<List<KeyValue<String, List<PurchaseWithStatus>>>> resultMono = Mono.justOrEmpty(cardPurchaseService.getAllCardPurchases());
		return resultMono.flatMap(purchases -> ServerResponse.ok().bodyValue(purchases));
	}

}
