package com.hubEleven.deliveryManager.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {

	// deliveryType에 따른 최대 순번값 조회
	Integer findMaxDeliveryOrderByDeliveryType(DeliveryType type);

	DeliveryManager save(DeliveryManager deliveryManager);

	Optional<DeliveryManager> findById(Long id);

	List<DeliveryManager> findAll();

	List<DeliveryManager> findAllByHubId(UUID hubId);

    Integer findMaxDeliveryOrderByHubId(UUID hubId);

}
