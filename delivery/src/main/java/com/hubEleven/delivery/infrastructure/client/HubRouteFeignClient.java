package com.hubEleven.delivery.infrastructure.client;

import com.hubEleven.delivery.infrastructure.dto.HubRouteFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hubRoute")
public interface HubRouteFeignClient {
    @GetMapping("/v1/hubRoute")
    List<HubRouteFeignResponseDto> getRoute(@RequestParam UUID fromHubId, @RequestParam UUID toHubId);
}
