package com.hubEleven.deliveryManager.application.service;

import com.hubEleven.common.exception.GlobalException;
import com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode;
import com.hubEleven.deliveryManager.infrastructure.client.HubFeignClient;
import com.hubEleven.deliveryManager.infrastructure.dto.HubResponseDto;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class HubService {
    private final HubFeignClient hubFeignClient;

    public HubService(HubFeignClient hubFeignClient) {
        this.hubFeignClient = hubFeignClient;
    }

    public HubResponseDto getHub(UUID hubId) {
        try {
            return hubFeignClient.getHub(hubId);
        } catch (Exception e) {
            // 로깅, 예외 변환 등
            throw new GlobalException(DeliveryManagerErrorCode.HUB_NOR_FOUND);
        }
    }
}
