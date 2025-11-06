package com.hubEleven.delivery.infrastructure.client;

import com.hubEleven.delivery.infrastructure.dto.UserFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user")
public interface UserFeignClient {
    @GetMapping("/v1/user")
    UserFeignResponseDto getUser(
            @RequestParam UUID companyId, @RequestParam String deliveryType);
}
