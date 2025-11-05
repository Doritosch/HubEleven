package com.hubEleven.delivery.application.client;

import com.hubEleven.delivery.application.dto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order")
public interface OrderFeignClient {
    @GetMapping("/v1/order/{orderId}")
    OrderResponseDto getOrder(@PathVariable UUID orderId);
}
