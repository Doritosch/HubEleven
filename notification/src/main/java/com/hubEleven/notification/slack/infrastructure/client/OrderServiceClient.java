package com.hubEleven.notification.slack.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/v1/orders/{orderId}")
    OrderDTO getOrder(@PathVariable UUID orderId);

    record OrderDTO(UUID orderId) {}
}
