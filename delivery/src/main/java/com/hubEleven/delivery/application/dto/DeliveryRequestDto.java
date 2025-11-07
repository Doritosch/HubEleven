package com.hubEleven.delivery.application.dto;

import com.hubEleven.delivery.domain.DeliveryStatus;
import java.util.UUID;

public record DeliveryRequestDto(
        UUID orderId,
        DeliveryStatus status,
        UUID fromHubId,
        UUID toHubId,
        String recipientName,
        String recipientSlackId,
        Long deliveryManagerId) {

}
