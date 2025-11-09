package com.hubEleven.delivery.infrastructure.client;

import com.hubEleven.delivery.infrastructure.dto.DeliveryMangerFeignResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "delivery-service")
public interface DeliveryManagerFeignClient {
	@GetMapping("/v1/deliveryManager")
	DeliveryMangerFeignResponseDto getDeliveryManager(
			// todo 권한 enum 완성되면 수정 예정
			@RequestParam UUID companyId, @RequestParam String companyManager);
}
