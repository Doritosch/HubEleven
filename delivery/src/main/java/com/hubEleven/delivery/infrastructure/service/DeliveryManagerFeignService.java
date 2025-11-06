package com.hubEleven.delivery.infrastructure.service;

import com.hubEleven.delivery.infrastructure.client.DeliveryManagerFeignClient;
import com.hubEleven.delivery.infrastructure.dto.DeliveryMangerFeignResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DeliveryManagerFeignService {
    private final DeliveryManagerFeignClient deliveryManagerFeignClient;

    public DeliveryMangerFeignResponseDto getDeliveryManagerInfo(UUID companyId){
        return deliveryManagerFeignClient.getDeliveryManager(companyId, "companyManager");
    }

}
