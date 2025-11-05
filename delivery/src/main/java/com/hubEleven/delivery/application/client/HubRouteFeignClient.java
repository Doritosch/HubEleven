package com.hubEleven.delivery.application.client;

import com.hubEleven.delivery.application.dto.CompanyResponseDto;
import com.hubEleven.delivery.application.dto.HubRouteResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hubRoute")
public interface HubRouteFeignClient {
    @GetMapping("/v1/hubRoute")
    List<HubRouteResponseDto> getRoute(@RequestParam UUID fromHubId, @RequestParam UUID toHubId);
}
