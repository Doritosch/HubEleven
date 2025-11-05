package com.hubEleven.delivery.application;

import com.hubEleven.delivery.application.client.CompanyFeignClient;
import com.hubEleven.delivery.application.client.HubRouteFeignClient;
import com.hubEleven.delivery.application.client.OrderFeignClient;
import com.hubEleven.delivery.application.dto.DeliveryRequestDto;
import com.hubEleven.delivery.application.dto.DeliveryResponseDto;
import com.hubEleven.delivery.application.dto.HubRouteResponseDto;
import com.hubEleven.delivery.application.dto.OrderResponseDto;
import com.hubEleven.delivery.domain.Delivery;
import com.hubEleven.delivery.domain.DeliveryRepository;
import com.hubEleven.delivery.domain.DeliveryStatus;
import com.hubEleven.deliveryRoute.application.DeliveryRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final CompanyFeignClient companyFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final HubRouteFeignClient hubRouteFeignClient;
    private final DeliveryRouteService deliveryRouteService;

    // Delivery 조회
    private Delivery delivery(UUID deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(()->new IllegalArgumentException("유효하지 않은 배달 ID입니다."));
    }

    // 배달 리스트를 DeliveryResponseDto로 일괄 변환
    public List<DeliveryResponseDto> toDto(Page<Delivery> deliveryPage){
        return deliveryPage.stream()
                .map(DeliveryResponseDto::from)
                .collect(Collectors.toList());
    }

    // 배송 목록 조회
    public List<DeliveryResponseDto> getDeliveryList(int page, int size, String sort) {
        // 1. Pageable 객체 생성 (정렬 기준 : sort)
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());

        // 2. Repository에서 페이지 & 정렬하여 배달 목록 조회
        Page<Delivery> deliveryPage = deliveryRepository.findAll(pageable);

        // 3. Entity -> DTO 반환
        return toDto(deliveryPage);
    }

    // 배송 상세 조회
    public Delivery getDelivery(UUID deliveryId) {
        return delivery(deliveryId);
    }

    // 배송 생성
    public void createDelivery(UUID orderId, DeliveryRequestDto deliveryRequestDto) {
       // 주문 ID를 기준으로 주문 정보 가져오기
        OrderResponseDto order = orderFeignClient.getOrder(orderId);

        UUID fromCompanyId = order.requestorCompnayId();    // 요청업체
        UUID toCompanyId = order.recipientCompanyId();      // 수령 업체

       // 배송 생성 -> 주문 ID로 검색해서 받아오는 방식으로 변경 예정
       Delivery delivery = new Delivery();
       delivery.setOrderId(orderId);
       delivery.setStatus(DeliveryStatus.HUB_WAITHING);
       delivery.setFromHubId(fromCompanyId);
       delivery.setToHubId(toCompanyId);

       deliveryRepository.save(delivery);

       // 요청업체의 관리 허브 ID는 업체 테이블에 있음
       // 주문 정보에 있는 요청 업체 소속 허브를 출발 허브로 수령 업체 소속 허브를 도착 허브로 생각하고
       // 허브 경로에서 출발 허브 부터 도착 허브의 경로를 받아와서 그 갯수 만큼 배송 경로 생성
        UUID fromHubId = companyFeignClient.getHubId(fromCompanyId).HubId();
        UUID toHubId = companyFeignClient.getHubId(toCompanyId).HubId();

        // 허브 경로에 출발허브ID 와 도착허브ID를 넘기고 경로를 받는다.
        List<HubRouteResponseDto> hubRoute = hubRouteFeignClient.getRoute(fromHubId, toHubId);
        for(int seq = 0; seq <hubRoute.size(); seq++){
            HubRouteResponseDto deliveryRoute = hubRoute.get(seq);
            // 배송 경로 생성 요청
            deliveryRouteService.createDeliveryRoute(delivery, deliveryRoute, seq);
        }
    }

    // 배송 수정
    public void updateDelivery(UUID deliveryId, DeliveryRequestDto requestDto) {
        Delivery delivery = delivery(deliveryId);

        delivery.setStatus(requestDto.status());                        // 상태 변경
        delivery.setFromHubId(requestDto.fromHubId());                  // 출발 허브
        delivery.setToHubId(requestDto.toHubId());                      // 도착 허브
        delivery.setRecipientName(requestDto.recipientName());          // 수령인
        delivery.setRecipientSlackId(requestDto.recipientSlackId());    // 수령인 SlackId
        delivery.setDeliveryManagerId(requestDto.deliveryManagerId());  // 배달 담당자
    }

    // 배송 삭제
    public void deleteDelivery(UUID deliveryId) {
        // 1. 배송 정보 삭제 (논리삭제)로 변경 예정
        deliveryRepository.deleteById(deliveryId);
    }

}
