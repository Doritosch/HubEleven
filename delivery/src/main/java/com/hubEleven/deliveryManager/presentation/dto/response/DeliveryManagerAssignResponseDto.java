package com.hubEleven.deliveryManager.presentation.dto.response;

import com.hubEleven.deliveryManager.domain.DeliveryManager;
import java.util.List;

public record DeliveryManagerAssignResponseDto(
		List<DeliveryManagerInfo> hubDeliveryManager, DeliveryManagerInfo companyDeliveryManager) {

	public static DeliveryManagerAssignResponseDto of(
			List<DeliveryManager> hubDeliveryManager, DeliveryManager companyDeliveryManager) {
		return new DeliveryManagerAssignResponseDto(
				hubDeliveryManager.stream().map(DeliveryManagerInfo::from).toList(),
				DeliveryManagerInfo.from(companyDeliveryManager));
	}
}
