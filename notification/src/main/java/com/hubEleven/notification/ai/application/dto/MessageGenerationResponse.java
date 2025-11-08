package com.hubEleven.notification.ai.application.dto;

// Ai가 산출한 최종 발송 시한 결과
public record MessageGenerationResponse(String finalDispatchDeadline, String messageBody) {
	public static MessageGenerationResponse of(String finalDispatchDeadline, String messageBody) {
		return new MessageGenerationResponse(finalDispatchDeadline, messageBody);
	}
}
