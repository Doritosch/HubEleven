package com.hubEleven.delivery.application.client;

import com.hubEleven.delivery.application.dto.CompanyResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company")
public interface CompanyFeignClient {
    @GetMapping("/v1/hub/{companyId}")
    CompanyResponseDto getHubId(@PathVariable UUID companyId);

}
