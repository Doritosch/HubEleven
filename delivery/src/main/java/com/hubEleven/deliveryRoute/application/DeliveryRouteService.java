package com.hubEleven.deliveryRoute.application;

import com.hubEleven.delivery.infrastructure.dto.HubRouteFeignResponseDto;
import com.hubEleven.delivery.domain.Delivery;
import com.hubEleven.delivery.domain.DeliveryStatus;
import com.hubEleven.deliveryRoute.domain.DeliveryRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    // 배달 경로 생성
    public void createDeliveryRoute (Delivery delivery, HubRouteFeignResponseDto routeDto, int seq){
        DeliveryRoute deliveryRoute = DeliveryRoute.builder()
                .delivery(delivery)
                .seq(seq)
                .fromHubId(routeDto.fromHubId())
                .toHubId(routeDto.toHubId())
                .deliveryManagerId(delivery.getDeliveryManagerId())
                .status(DeliveryStatus.HUB_WAITHING)
                .expectedDistance(routeDto.distance())
                .expectedDuration(routeDto.duration())
                .build();
    }
}
