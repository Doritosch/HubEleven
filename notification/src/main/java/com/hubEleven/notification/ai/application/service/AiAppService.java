package com.hubEleven.notification.ai.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubEleven.common.exception.GlobalException;
import com.hubEleven.notification.ai.application.dto.MessageGenerationRequest;
import com.hubEleven.notification.ai.application.dto.MessageGenerationResponse;
import com.hubEleven.notification.ai.domain.exception.NotificationErrorCode;
import com.hubEleven.notification.ai.domain.model.AiRequestLog;
import com.hubEleven.notification.ai.domain.repository.AiRequestLogRepository;
import com.hubEleven.notification.ai.domain.service.PromptDomainService;
import com.hubEleven.notification.ai.infrastructure.client.GeminiClient;
import com.hubEleven.notification.ai.infrastructure.client.dto.GeminiResponse;
import com.hubEleven.notification.ai.infrastructure.configuration.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiAppService {

	private final AiRequestLogRepository aiRequestLogRepository;
	private final PromptDomainService promptDomainService;
	private final GeminiClient geminiClient;
	private final AiProperties aiProperties;
	private final ObjectMapper objectMapper;

	@Transactional
	public MessageGenerationResponse generateDispatchGuidance(MessageGenerationRequest request) {
		if (aiRequestLogRepository.existsByOrderId(request.orderId())) {
			throw new GlobalException(NotificationErrorCode.AI_REQUEST_DUPLICATED);
		}

		String prompt = promptDomainService.buildDispatchGuidancePrompt(request);
		AiRequestLog log =
				aiRequestLogRepository.save(AiRequestLog.requested(request.orderId(), prompt));
		String metadata = serialize(request);

		try {
			GeminiResponse response =
					geminiClient.generate(aiProperties.model(), aiProperties.api().key(), prompt);
			String raw = response.primaryText();

			MessageGenerationResponse result = parseResponse(raw);
			log.success(raw, metadata);
			return result;
		} catch (GlobalException ex) {
			log.fail(ex.getMessage(), metadata);
			throw ex;
		} catch (Exception ex) {
			log.fail(ex.getMessage(), metadata);
			throw new GlobalException(NotificationErrorCode.AI_GENERATION_FAIL);
		}
	}

	private MessageGenerationResponse parseResponse(String rawJson) {
		try {
			ResponsePayload payload = objectMapper.readValue(rawJson, ResponsePayload.class);
			return MessageGenerationResponse.success(
					payload.finalDispatchDeadline(), payload.messageBody());
		} catch (JsonProcessingException e) {
			throw new GlobalException(NotificationErrorCode.AI_RESPONSE_PARSE_FAIL);
		}
	}

	private String serialize(MessageGenerationRequest request) {
		try {
			return objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	private record ResponsePayload(String finalDispatchDeadline, String messageBody) {}
}
