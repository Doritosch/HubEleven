package com.hubEleven.delivery.infrastructure.dto;

import java.util.UUID;

public record OrderFeignResponseDto(
    UUID orderId,
    UUID requestorCompanyId,
    UUID recipientCompanyId
) {
}
