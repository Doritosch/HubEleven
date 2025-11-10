package com.hubEleven.deliveryManager.presentation;

import com.hubEleven.common.request.CommonPageRequest;
import com.hubEleven.common.response.ApiResponse;
import com.hubEleven.common.response.ApiResponseEntity;
import com.hubEleven.common.response.CommonPageResponse;
import com.hubEleven.deliveryManager.application.DeliveryManagerService;
import com.hubEleven.deliveryManager.presentation.dto.request.DeliveryManagerAssignRequestDto;
import com.hubEleven.deliveryManager.presentation.dto.request.DeliveryManagerCreateRequestDto;
import com.hubEleven.deliveryManager.presentation.dto.response.DeliveryManagerAssignResponseDto;
import com.hubEleven.deliveryManager.presentation.dto.response.DeliveryManagerResponseDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/delivery-managers")
public class DeliveryManagerController {

	private final DeliveryManagerService deliveryManagerService;

	public DeliveryManagerController(DeliveryManagerService deliveryManagerService) {
		this.deliveryManagerService = deliveryManagerService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<DeliveryManagerResponseDto>> createDeliveryManager(
			@RequestBody @Valid DeliveryManagerCreateRequestDto createRequestDto) {
		// TODO: JWT토큰 파싱하여 권한 검증
		log.info("[DeliveryManager Controller] 배달 담당자 생성 요청");
		DeliveryManagerResponseDto responseDto =
				deliveryManagerService.createDeliveryManager(createRequestDto);

        return ApiResponseEntity.success(responseDto);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<CommonPageResponse<DeliveryManagerResponseDto>>>
    getAllDeliveryManager(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "CREATED_AT") CommonPageRequest.SortType sortType,
        @RequestParam(defaultValue = "DESC") Sort.Direction direction,
        @RequestParam(required = false) String keyword) {

        log.info("[DeliveryManager Controller] 배달 담당자 목록 조회 요청");

        CommonPageRequest pageRequest = new CommonPageRequest(page, size, sortType, direction, keyword);
        Page<DeliveryManagerResponseDto> deliveryManagerList =
				deliveryManagerService.getAllDeliveryManager(pageRequest.toPageable());

        CommonPageResponse<DeliveryManagerResponseDto> responseDtoList =
            CommonPageResponse.of(deliveryManagerList);
        return ApiResponseEntity.success(responseDtoList);
	}

	@GetMapping("/{managerId}")
    public ResponseEntity<ApiResponse<DeliveryManagerResponseDto>> getDeliveryManager(
			@PathVariable Long managerId) {
		log.info("[DeliveryManager Controller] 배달 담당자 단일 조회 요청");
		DeliveryManagerResponseDto responseDto = deliveryManagerService.getDeliveryManager(managerId);
        return ApiResponseEntity.success(responseDto);
	}

	@DeleteMapping("/{managerId}")
    public ResponseEntity<ApiResponse<Void>> deleteDeliveryManager(@PathVariable Long managerId) {
		deliveryManagerService.deleteDeliveryManager(managerId);
        return ApiResponseEntity.ok("삭제가 완료되었습니다.");
	}

	@PatchMapping("/assign")
	public ResponseEntity<DeliveryManagerAssignResponseDto> assignDeliveryManager(
			@RequestBody @Valid DeliveryManagerAssignRequestDto assignRequestDto) {
		DeliveryManagerAssignResponseDto assignResponseDtoList =
				deliveryManagerService.assignDeliveryManagers(assignRequestDto);

		return ResponseEntity.ok(assignResponseDtoList);
	}


    //MSA 서버 내부용 메서드
    @GetMapping("/exist")
    public boolean checkDeliveryManagerExists(Long managerId) {
        return deliveryManagerService.checkDeliveryManagerExists(managerId);

    }

    //MSA 서버 내부용 메서드
    //업체배송담당 매니저 소속확인 - 해당허브에 해당매니저가 존재하는지 검증
    @GetMapping("/exist/hub")
    public boolean hasDeliveryManagerInHub(UUID hubId, Long managerId) {
        return deliveryManagerService.hasDeliveryManagerInHub(hubId, managerId);

    }

//search
//    public ResponseEntity<ApiResponse<CommonPageResponse<DeliveryManagerResponseDto>>> search(
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "10") int size,
//        @RequestParam(defaultValue = "CREATED_AT") CommonPageRequest.SortType sortType,
//        @RequestParam(defaultValue = "DESC") Sort.Direction direction,
//        @RequestParam(required = false) String keyword ) {
//    }

}
