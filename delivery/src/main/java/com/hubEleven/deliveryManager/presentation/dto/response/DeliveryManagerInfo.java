package com.hubEleven.deliveryManager.presentation.dto.response;

import com.hubEleven.deliveryManager.domain.DeliveryManager;
import java.util.UUID;

public record DeliveryManagerInfo(
		Long deliveryManagerId, UUID hubId, String slackId, String deliveryType) {
	public static DeliveryManagerInfo from(DeliveryManager deliveryManager) {
		return new DeliveryManagerInfo(
				deliveryManager.getDeliveryManagerId(),
				deliveryManager.getHubId(),
				deliveryManager.getSlackId(),
				deliveryManager.getDeliveryType().name());
	}
}
