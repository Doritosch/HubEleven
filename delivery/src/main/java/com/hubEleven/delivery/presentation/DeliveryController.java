package com.hubEleven.delivery.presentation;

import com.hubEleven.delivery.application.dto.DeliveryRequestDto;
import com.hubEleven.delivery.application.dto.DeliveryResponseDto;
import com.hubEleven.delivery.application.DeliveryService;
import com.hubEleven.delivery.domain.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 배송 검색
    @GetMapping("/search")
    public String searchDelivery(
            @RequestParam("deliveryId") UUID deliveryId,
            @RequestParam(value = "page", defaultValue = "0") int page,             // 페이지 번호
            @RequestParam(value = "size", defaultValue = "10") int size,            // 조회할 항목수
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort   // 정렬기준
    ){
        return "delivery";
    }

    // 배송 전체 조회
    @GetMapping
    public String getDeliveryList(
            @RequestParam(value = "page", defaultValue = "0") int page,             // 페이지 번호
            @RequestParam(value = "size", defaultValue = "10") int size,            // 조회할 항목수
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort   // 정렬기준
    ) {
        List<DeliveryResponseDto> result = deliveryService.getDeliveryList(page, size, sort);
        return "result";
    }

    // 배송 상세 조회
    @GetMapping("/{deliveryId}")
    public String getDelivery(
            @PathVariable UUID deliveryId
    ) {
        Delivery result = deliveryService.getDelivery(deliveryId);
        return "delivery";
    }

    // 배송 생성
    @PostMapping
    public String createDelivery(
            @RequestBody DeliveryRequestDto deliveryRequestDto
    ){
        UUID orderId = deliveryRequestDto.orderId();
        deliveryService.createDelivery(orderId, deliveryRequestDto );
        return "delivery";
    }

    // 배송 수정
    @PatchMapping("/{deliveryId}")
    public String updateDelivery(
            @PathVariable UUID deliveryId,
            @RequestBody DeliveryRequestDto deliveryRequestDto
    ){
        deliveryService.updateDelivery(deliveryId,deliveryRequestDto);
        return "delivery";
    }

    // 배송 삭제
    @DeleteMapping("/{deliveryId}")
    public String deleteDelivery(
            @PathVariable UUID deliveryId
    ){
        deliveryService.deleteDelivery(deliveryId);
         return "delivery";
    }


}
