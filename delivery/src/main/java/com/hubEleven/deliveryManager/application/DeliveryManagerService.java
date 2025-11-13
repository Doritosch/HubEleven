package com.hubEleven.deliveryManager.application;

import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.ACCESS_DENIED;
import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_NOT_FOUND;
import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.DELIVERY_MANAGER_NOT_FOUND;
import static com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode.HUB_DELIVERY_MANAGER_NOT_FOUND;

import com.commonLib.common.exception.GlobalException;
import com.hubEleven.deliveryManager.application.service.HubService;
import com.hubEleven.deliveryManager.application.service.OrderService;
import com.hubEleven.deliveryManager.application.service.UserService;
import com.hubEleven.deliveryManager.domain.DeliveryManager;
import com.hubEleven.deliveryManager.domain.DeliveryManagerRepository;
import com.hubEleven.deliveryManager.domain.DeliveryType;
import com.hubEleven.deliveryManager.domain.exception.DeliveryManagerErrorCode;
import com.hubEleven.deliveryManager.infrastructure.dto.HubResponseDto;
import com.hubEleven.deliveryManager.infrastructure.dto.OrderResponse;
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
import org.springframework.dao.DataIntegrityViolationException;
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
	private final UserService userService;
	private final OrderService orderService;

	public DeliveryManagerService(
			DeliveryManagerRepository deliveryManagerRepository,
			HubService hubService,
			UserService userService,
			OrderService orderService) {
		this.deliveryManagerRepository = deliveryManagerRepository;
		this.hubService = hubService;
		this.userService = userService;
		this.orderService = orderService;
	}

	@Transactional
	public DeliveryManagerResponseDto createDeliveryManager(
			DeliveryManagerCreateRequestDto
					createRequestDto /*, Long requestUserId, String requestUserRole */) {

		log.info("[DeliveryManager Service] 배송담당자 생성 요청 서비스 진입");

		// 임시---------------------------------------------------------------------------
		// 로그인한 유저 정보
		Long requestUserId = 1L; // id
		String requestUserRole = "MASTER"; // role
		UUID companyId = UUID.randomUUID(); // 소속ID

		// 생성할 유저 정보
		Long id = createRequestDto.deliveryManagerId(); // 생성 요청 id
		// UserInfoResponse response = userService.getUser(id,requestUserId, requestUserRole);

		// log.info("feignClient - Usr 통신 정보 : {} ", String.valueOf(response.userId()));

		// feignClient로 받아온 유저정보 가정
		UUID hubId = UUID.fromString("97eb5e60-beee-11f0-adaf-c6cdb3175b81");
		String slackId = "slack001";
		DeliveryType deliveryType = DeliveryType.COMPANY;
		// -------------------------------------------------------------------------------

		// 로그인한 유저가 허브매니저일 때 유저정보에서 이 유저의 소속 업체 가져와 권한이 있는지 확인
		// 로그인한 유저 정보
		if (requestUserRole == "HUB_MANAGER" && companyId != hubId) {
			throw new GlobalException(ACCESS_DENIED);
		}

		if (checkDeliveryManagerExists(id)) {
			throw new GlobalException(DeliveryManagerErrorCode.DUPLICATE_DELIVERY_MANAGER);
		}

		int retryCount = 0;
		final int maxRetry = 5;
		while (true) {
			try {

				Integer last = null;
				if (hubId == null) {
					last = deliveryManagerRepository.findLastDeliveryOrderForNullHubForUpdate();
				} else {
					last = deliveryManagerRepository.findLastDeliveryOrderByHubIdForUpdate(hubId);
				}

				int nextOrder = (last == null) ? 1 : last + 1;

				DeliveryManager deliveryManager =
						DeliveryManager.create(id, hubId, slackId, deliveryType, nextOrder);

				deliveryManagerRepository.save(deliveryManager);

				return DeliveryManagerResponseDto.from(deliveryManager);

			} catch (DataIntegrityViolationException ex) {
				// 유니크 충돌 발생 시 재시도 (다른 트랜잭션이 먼저 삽입했을 수 있음)
				if (++retryCount > maxRetry) {
					throw new GlobalException(DeliveryManagerErrorCode.DUPLICATE_DELIVERY_MANAGER);
				}
				log.warn("[createDeliveryManager] 순번 충돌 발생, 재시도 {}/{}", retryCount, maxRetry);
				try {
					Thread.sleep(50);
				} catch (InterruptedException ignored) {
				}
				// 재시도: 같은 트랜잭션으로는 안되고, 루프가 계속되며 다음 반복에서 새 트랜잭션으로 다시 시도됨 (새트랜잭션이 생기는지 확인필요)
			}
		}
	}

	@Transactional(readOnly = true)
	public Page<DeliveryManagerResponseDto> getAllDeliveryManager(Pageable pageable) {

		// if() 권한=MASTER
		Page<DeliveryManager> deliveryManagerList = deliveryManagerRepository.findAll(pageable);

		// if 권한=HubManager
		// 유저조회해서 담당 hubID 가져오기
		// Page<DeliveryManager> hubDeliveryManagerList =
		// deliveryManagerRepository.findAllByHubIdANdDeletedAtISNULL(hubId);

		return deliveryManagerList.map(DeliveryManagerResponseDto::from);
	}

	@Transactional(readOnly = true)
	public DeliveryManagerResponseDto getDeliveryManager(Long managerId) {

		Optional<DeliveryManager> deliveryManager;
		// MASTER
		deliveryManager = deliveryManagerRepository.findById(managerId);

		// TODO: 허브담당자 (유저에서 소속업체 id가져와서 조건걸어서 조회)
		// deliveryManager =
		// deliveryManagerRepository.findByDeliveryManagerIdAndDeletedAtIsNull(managerId);

		// TODO: 본인(로그인정보에서 본인id 로 조회)
		// deliveryManager =
		// deliveryManagerRepository.findByDeliveryManagerIdAndDeletedAtIsNull(managerId);

		return deliveryManager
				.map(DeliveryManagerResponseDto::from)
				.orElseThrow(() -> new GlobalException(DELIVERY_MANAGER_NOT_FOUND));
	}

	@Transactional
	public void deleteDeliveryManager(Long managerId) {
		// TODO: 삭제권한 검증 - 마스터는 전부삭제가능, 허브매니저는 본인 허브 배송담당자만 삭제 가능
		DeliveryManager deliveryManager;

		// if MASTER
		deliveryManager =
				deliveryManagerRepository
						.findById(managerId)
						.orElseThrow(() -> new GlobalException(DELIVERY_MANAGER_NOT_FOUND));

		// if HUB_MANAGER
		// 로그인한 사용자 정보를 user에서 받아와서 companyId(소속정보) 확인
		// if(!deliveryManager.getHubId().equals(companyId)) throw new GlobalException(ACCESS_DENIED);

		// 이미삭제된 데이터먼 에러발생
		if (deliveryManagerRepository
				.findByDeliveryManagerIdAndDeletedAtIsNotNull(managerId)
				.isPresent()) {
			throw new GlobalException(DELIVERY_MANAGER_NOT_FOUND);
		}

		// 임시 데이터
		Long deletedBy = 1L;

		deliveryManager.delete(deletedBy);

		deliveryManagerRepository.save(deliveryManager);
	}

	@Transactional
	public DeliveryManagerAssignResponseDto assignDeliveryManagers(
			DeliveryManagerAssignRequestDto assignRequestDto) {

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
		OrderResponse orderResponse = orderService.getOrder(orderId);

		log.info(orderResponse.recipientCompanyId().toString());
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

	public boolean hasDeliveryManagerInHub(UUID hubId, Long managerId) {
		return deliveryManagerRepository.existsByHubIdAndDeliveryManagerId(hubId, managerId);
	}

	public Page<DeliveryManagerResponseDto> searchDeliveryManager(
			UUID deliveryManagerId,
			UUID hubId,
			DeliveryType deliveryType,
			String slackId,
			Pageable pageable) {

		Specification<DeliveryManager> spec =
				(root, query, cb) -> {
					List<Predicate> predicates = new ArrayList<>();

					if (deliveryManagerId != null)
						predicates.add(cb.equal(root.get("id"), deliveryManagerId));
					if (hubId != null) predicates.add(cb.equal(root.get("hubId"), hubId));
					if (deliveryType != null)
						predicates.add(cb.equal(root.get("deliveryType"), deliveryType));
					if (slackId != null) predicates.add(cb.like(root.get("slackId"), "%" + slackId + "%"));

					return cb.and(predicates.toArray(new Predicate[0]));
				};

		Page<DeliveryManager> deliveryManagerPage = deliveryManagerRepository.findAll(spec, pageable);

		return deliveryManagerPage.map(DeliveryManagerResponseDto::from);
	}
}
