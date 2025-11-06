package com.hubEleven.delivery.infrastructure.client;

import com.hubEleven.delivery.infrastructure.dto.CompanyFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company")
public interface CompanyFeignClient {
    @GetMapping("/v1/hub/{companyId}")
    CompanyFeignResponseDto getHubId(@PathVariable UUID companyId);

}
