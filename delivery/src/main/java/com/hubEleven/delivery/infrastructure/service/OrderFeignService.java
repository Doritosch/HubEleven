package com.hubEleven.delivery.infrastructure.service;

import com.hubEleven.delivery.infrastructure.client.OrderFeignClient;
import com.hubEleven.delivery.infrastructure.dto.OrderFeignResponseDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderFeignService {
	private final OrderFeignClient orderFeignClient;

	public OrderFeignResponseDto getOrderInfo(UUID orderId) {
		return orderFeignClient.getOrder(orderId);
	}
}
