package com.hubEleven.deliveryManager.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryManagerRepository {

	DeliveryManager save(DeliveryManager deliveryManager);

	Optional<DeliveryManager> findById(Long id);

	Page<DeliveryManager> findAll(Pageable pageable);

    Page<DeliveryManager> findAllByHubId(UUID hubId, Pageable pageable);

    Integer findMaxDeliveryOrderByHubId(UUID hubId);

	// 허브 담당자용 (hubId가 null인 경우)
	Optional<DeliveryManager> findFirstByHubIdIsNullOrderByLastDeliveryTimeAscDeliveryOrderAsc();

	// 회사 담당자용 (hubId 지정)
	Optional<DeliveryManager> findFirstByHubIdOrderByLastDeliveryTimeAscDeliveryOrderAsc(UUID hubId);

    boolean existsByHubIdAndDeliveryManagerId(UUID hubId, Long managerId);

}
