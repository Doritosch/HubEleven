package com.hubEleven.notification.ai.infrastructure.client;

import com.commonLib.common.exception.GlobalException;
import com.hubEleven.notification.ai.domain.exception.NotificationErrorCode;
import com.hubEleven.notification.ai.infrastructure.client.dto.GeminiRequest;
import com.hubEleven.notification.ai.infrastructure.client.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {
	@Qualifier("geminiWebClient")
	private final WebClient geminiWebClient;

	public GeminiResponse generate(String model, String apiKey, String prompt) {
		if (apiKey == null || apiKey.isBlank()) {
			log.error("Gemini API key is missing/blank");
			throw new GlobalException(NotificationErrorCode.AI_BAD_REQUEST);
		}

		GeminiRequest body = GeminiRequest.fromPrompt(prompt);

		return geminiWebClient.post()
				.uri(uri -> uri.path("/v1beta/models/{model}:generateContent")
						.queryParam("key", apiKey)
						.build(model))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.exchangeToMono(resp -> {
					var sc = resp.statusCode();
					if (sc.is2xxSuccessful()) {
						return resp.bodyToMono(GeminiResponse.class);
					}
					return resp.bodyToMono(String.class).defaultIfEmpty("")
							.flatMap(bodyStr -> {
								log.error("Gemini HTTP error. status={} body={}", sc.value(), bodyStr);
								int s = sc.value();
								NotificationErrorCode code =
										(s == 429) ? NotificationErrorCode.AI_RATE_LIMITED :
												(s == 401 || s == 403) ? NotificationErrorCode.AI_BAD_REQUEST :
														(s == 400 || s == 404) ? NotificationErrorCode.AI_BAD_REQUEST :
																NotificationErrorCode.AI_UPSTREAM_UNAVAILABLE;
								return Mono.error(new GlobalException(code));
							});
				})
				.timeout(Duration.ofSeconds(60))
				.doOnSubscribe(sub -> log.info("Calling Gemini model={} promptLen={}", model, prompt.length()))
				.doOnError(e -> log.error("Gemini call failed", e))
				.block();
	}
}
