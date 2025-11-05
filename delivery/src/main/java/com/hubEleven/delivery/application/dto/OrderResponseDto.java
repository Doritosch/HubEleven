package com.hubEleven.delivery.application.dto;

import java.util.UUID;

public record OrderResponseDto(
    UUID orderId,
    UUID requestorCompnayId,
    UUID recipientCompanyId
) {
}
