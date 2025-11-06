package com.hubEleven.delivery.infrastructure.client;

import com.hubEleven.delivery.infrastructure.dto.OrderFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "order")
public interface OrderFeignClient {
    @GetMapping("/v1/order")
    OrderFeignResponseDto getOrder(@RequestParam UUID orderId);
}
