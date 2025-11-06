package com.hubEleven.delivery.infrastructure.client;

import com.hubEleven.delivery.infrastructure.dto.DeliveryMangerFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "delivery_Manager")
public interface DeliveryManagerFeignClient {
    @GetMapping("/v1/deliveryManager")
    DeliveryMangerFeignResponseDto getDeliveryManager(
            // todo 권한 enum 완성되면 수정 예정
            @RequestParam UUID companyId, @RequestParam String companyManager
    );
}
