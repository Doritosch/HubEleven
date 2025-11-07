package com.hubEleven.notification.application.dto;

import jakarta.validation.constraints.NotBlank;

//Ai가 산출한 최종 발송 시한 결과
public record MessageGenerationResponse(
        @NotBlank String finalDispatchDeadline
) {
    public static MessageGenerationResponse of(String finalDispatchDeadline) {
        return new MessageGenerationResponse(finalDispatchDeadline);
    }
}
