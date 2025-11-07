package com.hubEleven.notification.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//최종 발송 시한 산출을 위해 AI에게 전달할 주문/배송 컨텍스트
public record MessageGenerationRequest(
        @NotNull UUID orderId,
        @NotBlank String customerName,
        @Email String customerEmail,
        @NotNull @PastOrPresent LocalDateTime orderDateTime,
        @NotEmpty @Valid List<Item> items,
        @Size(max = 1000) String requestNote,
        @NotBlank String fromHub,
        @Valid List<String> viaHubs,
        @NotBlank String destination,
        @NotBlank String deliveryManagerName,
        @NotBlank @Email String deliveryManagerEmail
) {
    public static MessageGenerationRequest of(
            UUID orderId,
            String customerName,
            String customerEmail,
            LocalDateTime orderDateTime,
            List<Item> items,
            String requestNote,
            String fromHub,
            List<String> viaHubs,
            String destination,
            String deliveryManagerName,
            String deliveryManagerEmail
    ) {
        return new MessageGenerationRequest(
                orderId, customerName, customerEmail, orderDateTime, items, requestNote,
                fromHub, viaHubs, destination, deliveryManagerName, deliveryManagerEmail
        );
    }

    public record Item(
            @NotBlank @Size(max = 200) String name,
            @Positive int quantity
    ) {
        public static Item of(String name, int quantity) {
            return new Item(name, quantity);
        }
    }
}
