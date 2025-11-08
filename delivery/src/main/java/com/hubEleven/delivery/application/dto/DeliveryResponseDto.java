package com.hubEleven.delivery.application.dto;

import com.hubEleven.delivery.domain.Delivery;
import com.hubEleven.delivery.domain.DeliveryStatus;
import java.util.UUID;

public record DeliveryResponseDto(
		UUID deliveryId,
		UUID orderId,
		DeliveryStatus status,
		UUID fromHubId,
		UUID toHubId,
		String recipientName,
		String recipientSlackId,
		Long deliveryManagerId) {

	// Delivery 엔티티를 Dto로 변환하는 용도
	public static DeliveryResponseDto from(Delivery delivery) {
		return new DeliveryResponseDto(
				delivery.getId(),
				delivery.getOrderId(),
				delivery.getStatus(),
				delivery.getFromHubId(),
				delivery.getToHubId(),
				delivery.getRecipientName(),
				delivery.getRecipientSlackId(),
				delivery.getDeliveryManagerId());
	}
}
