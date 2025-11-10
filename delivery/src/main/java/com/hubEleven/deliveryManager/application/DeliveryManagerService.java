package com.hubEleven.deliveryManager.application;

import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_NOT_FOUND;
import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.DELIVERY_MANAGER_NOF_FOUND;
import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.HUB_DELIVERY_MANAGER_NOT_FOUND;

import com.commonLib.common.exception.GlobalException;
import com.hubEleven.deliveryManager.application.service.HubService;
import com.hubEleven.deliveryManager.domain.DeliveryManager;
import com.hubEleven.deliveryManager.domain.DeliveryManagerRepository;
import com.hubEleven.deliveryManager.domain.DeliveryType;
import com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode;
import com.hubEleven.deliveryManager.infrastructure.dto.HubResponseDto;
import com.hubEleven.deliveryManager.presentation.dto.request.DeliveryManagerAssignRequestDto;
import com.hubEleven.deliveryManager.presentation.dto.request.DeliveryManagerCreateRequestDto;
import com.hubEleven.deliveryManager.presentation.dto.response.DeliveryManagerAssignResponseDto;
import com.hubEleven.deliveryManager.presentation.dto.response.DeliveryManagerResponseDto;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DeliveryManagerService {

	private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubService hubService;

    public DeliveryManagerService(
        DeliveryManagerRepository deliveryManagerRepository, HubService hubService) {
        this.deliveryManagerRepository = deliveryManagerRepository;
        this.hubService = hubService;
    }

	// 배송 담당자 생성
	@Transactional
	public DeliveryManagerResponseDto createDeliveryManager(
			DeliveryManagerCreateRequestDto createRequestDto) {
		// plan : 요청 dto로 유저id만 받고, 유저서비스에서 id로 정보를 조회해 나머지 엔티티 필드 채우기(hubID, slackId, DeliveryType,
		// deliveryOrder(배송순번))
        /**
         * TODO : 1. 생성 권한이 있는지 확인하기 (로그인한 유저가 마스터 혹은 허브관리자인가?) -> 컨트롤러에서 2. RequestDto에서 id 꺼내와
         * FeignClient로 User-Service 서버에서 user정보 받아오기 (user정보에 id, slackId, 권한, 소속업체(허브)ID 존재) 3. 위에서
         * 받아온 user정보에서 id 존재 여부 조회 후, role이 배송담당자인지 확인, 3-2 소속업체 id가 NULL이면 허브배송 담당자, NULL이 아니면
         * 업체담당자(소속업체 id가 허브id가 맞는지도 검증해야 할까?)
         */
        log.info("[Delivery Manager Service] create: 배달 담당자 생성 요청");
		// 임시 데이터----------------------------
		Long id = createRequestDto.deliveryManagerId();
		UUID hubId = null; // UUID.fromString("1524dc79-9ed1-460b-a85f-521f0d8a28aa"); //null;
		// //UUID.fromString("9090dc79-9ed1-460b-a85f-521f0d8a28aa"); // UUID.randomUUID();
		String slackId = "slack001";
		DeliveryType deliveryType = DeliveryType.HUB;

        log.info("[Delivery Manager Service] create: 배송타입 : {}", deliveryType);
		// ----------------------------------

        // hubID 존재여부 검증
        if (deliveryType == DeliveryType.COMPANY) {
            HubResponseDto hubResponseDto = hubService.getHub(hubId);
            log.info(
                "[Delivery Manager Service] create:허브 정보 확인 : {}, {}",
                hubResponseDto.hubId(),
                hubResponseDto.name());
        }

        // DB에 이미 존재하는 id 인지 확인
        if (checkDeliveryManagerExists(id))
            throw new GlobalException(DeliveryManagerErrorCode.DUPLICATE_DELIVERY_MANAGER);

		// 배송순번 부여
		int deliveryOrder = setDeliveryOrder(deliveryType, hubId);

		DeliveryManager deliveryManager =
				DeliveryManager.create(id, hubId, slackId, deliveryType, deliveryOrder);

		deliveryManagerRepository.save(deliveryManager);

        log.info("[Delivery Manager Service] create: 배달 담당자 생성 - DB 저장 성공");

		DeliveryManagerResponseDto responseDto = DeliveryManagerResponseDto.from(deliveryManager);

		return responseDto;
	}

	@Transactional(readOnly = true)
	public Page<DeliveryManagerResponseDto> getAllDeliveryManager(Pageable pageable) {
        /** TODO: 검증사항 1. 조회 권한 검증(컨트롤러) 마스터, 허브담당자만 2. 세부 권한 검증(마스터는 전체조회 / 허브담당자는 본인허브 배달담당자만 조회 ) */

        // if() 권한=MASTER
        Page<DeliveryManager> deliveryManagerList = deliveryManagerRepository.findAll(pageable);

        // if 권한=HubManager
        // 유저조회해서 담당 hubID 가져오기
        // Page<DeliveryManager> hubDeliveryManagerList =
        // deliveryManagerRepository.findAllByHubId(hubId);

        // 아니면 접근불가

        return deliveryManagerList.map(DeliveryManagerResponseDto::from);
	}

	@Transactional(readOnly = true)
	public DeliveryManagerResponseDto getDeliveryManager(Long managerId) {
		/**
		 * TODO: 검증사항 1. 조회 권한 검증(컨트롤러) 2. 세부 권한 검증(마스터는 모두 조회 가능 / 허브담당자는 본인허브 배달담당자만 조회 / 배달담당자는 본인만
		 * 조회)
		 */
		Optional<DeliveryManager> deliveryManager = deliveryManagerRepository.findById(managerId);

        // TODO: 허브담당자 (유저에서 소속업체 id가져와서 조건걸어서 조회)

        // TODO: 본인(로그인정보에서 본인id 로 조회)

        return deliveryManager
                .map(DeliveryManagerResponseDto::from)
                .orElseThrow(() -> new GlobalException(DELIVERY_MANAGER_NOF_FOUND));

    }

	@Transactional
	public void deleteDeliveryManager(Long managerId) {
        // TODO: 삭제권한 검증 - 마스터는 전부삭제가능, 허브매니저는 본인 허브 배송담당자만 삭제 가능
		DeliveryManager deliveryManager =
				deliveryManagerRepository
						.findById(managerId)
                        .orElseThrow(() -> new GlobalException(DELIVERY_MANAGER_NOF_FOUND));

		// 임시 데이터
		Long deletedBy = 1L;
		deliveryManager.softDelete(deletedBy);
		deliveryManagerRepository.save(deliveryManager);
	}

	@Transactional
	public DeliveryManagerAssignResponseDto assignDeliveryManagers(
			DeliveryManagerAssignRequestDto assignRequestDto) {
        //TODO: api 요청한 유저(로그인한 유저)가 마스터인가?

		DeliveryType deliveryType = assignRequestDto.deliveryType();
		UUID hubId = assignRequestDto.hubId();

        // 해당 허브ID가 허브에 존재하는지 검증
        HubResponseDto hubResponseDto = hubService.getHub(hubId);
        log.info(
            "[Delivery Manager Service] assign:허브 정보 확인 : {}, {}",
            hubResponseDto.hubId(),
            hubResponseDto.name());

		DeliveryManager deliveryManager = getDeliveryManagerByType(deliveryType, hubId);
		deliveryManager.recordDeliveryTime();
		deliveryManagerRepository.save(deliveryManager);

        // TODO: 해당 주문이 실제존재하는직 검증
        UUID orderId = assignRequestDto.orderId();
		return DeliveryManagerAssignResponseDto.of(orderId, deliveryManager);
	}

	private int setDeliveryOrder(DeliveryType deliveryType, UUID hubId) {

		Integer maxOrder = deliveryManagerRepository.findMaxDeliveryOrderByHubId(hubId);

		int nextOrder = (maxOrder == null) ? 1 : maxOrder + 1;

		log.info("배송 타입: {}, 현재 마지막 순번: {}, 생성된 순번: {}", deliveryType, maxOrder, nextOrder);

		return nextOrder;
	}

	private DeliveryManager getDeliveryManagerByType(DeliveryType deliveryType, UUID hubId) {

		return switch (deliveryType) {
			case HUB -> deliveryManagerRepository
					.findFirstByHubIdIsNullOrderByLastDeliveryTimeAscDeliveryOrderAsc()
                    .orElseThrow(() -> new GlobalException(HUB_DELIVERY_MANAGER_NOT_FOUND));
			case COMPANY -> deliveryManagerRepository
					.findFirstByHubIdOrderByLastDeliveryTimeAscDeliveryOrderAsc(hubId)
                    .orElseThrow(() -> new GlobalException(COMPANY_DELIVERY_MANAGER_NOT_FOUND));
		};
	}


    public boolean checkDeliveryManagerExists(Long managerId) {
        return deliveryManagerRepository.findById(managerId).isPresent();
    }

    public boolean hasDeliveryManagerInHub(UUID hubId,  Long managerId) {
        return deliveryManagerRepository.existsByHubIdAndDeliveryManagerId(hubId,managerId);
    }

    public Page<DeliveryManagerResponseDto> searchDeliveryManager(
        UUID deliveryManagerId,
        UUID hubId,
        DeliveryType deliveryType,
        String slackId,
        Pageable pageable
    ) {

        Specification<DeliveryManager> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (deliveryManagerId != null)  predicates.add(cb.equal(root.get("id"), deliveryManagerId));
            if (hubId != null)  predicates.add(cb.equal(root.get("hubId"), hubId));
            if (deliveryType != null)  predicates.add(cb.equal(root.get("deliveryType"), deliveryType));
            if (slackId != null)  predicates.add(cb.like(root.get("slackId"),"%"+slackId+"%"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<DeliveryManager> deliveryManagerPage = deliveryManagerRepository.findAll(spec, pageable);

        return deliveryManagerPage.map(DeliveryManagerResponseDto::from);

    }
}
