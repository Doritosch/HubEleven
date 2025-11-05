package com.hubEleven.delivery.application.dto;

import com.hubEleven.delivery.domain.Delivery;
import com.hubEleven.delivery.domain.DeliveryStatus;

import java.util.UUID;

public record DeliveryResponseDto(
        UUID deliveryId,
        UUID orderId,
        DeliveryStatus status,
        UUID fromHubId,
        UUID toHubId,
        String recipientName,
        String recipientSlackId,
        Long deliveryManagerId
        ){
    public static  DeliveryResponseDto from(Delivery delivery){
        return new DeliveryResponseDto(
                delivery.getDeliveryId(),
                delivery.getOrderId(),
                delivery.getStatus(),
                delivery.getFromHubId(),
                delivery.getToHubId(),
                delivery.getRecipientName(),
                delivery.getRecipientSlackId(),
                delivery.getDeliveryManagerId()
        );
    }
}
