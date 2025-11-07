package com.hubEleven.deliveryManager.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface DeliveryManagerRepository {

    DeliveryManager save(DeliveryManager deliveryManager);

	Optional<DeliveryManager> findById(Long id);

	List<DeliveryManager> findAll();

	List<DeliveryManager> findAllByHubId(UUID hubId);

	Integer findMaxDeliveryOrderByHubId(UUID hubId);

	List<DeliveryManager> findNextAvailableManager(DeliveryType deliveryType, UUID hubId, Pageable pageable);

    DeliveryManager saveAndFlush(DeliveryManager deliveryManager);
    Integer findMaxDeliveryOrderByHubAndType(UUID hubId, DeliveryType type);
    List<DeliveryManager> findTop10ByDeliveryTypeAndHubIdOrderByLastDeliveryTimeAsc(DeliveryType type, UUID hubId);
    List<DeliveryManager> findTop10ByDeliveryTypeOrderByLastDeliveryTimeAsc(DeliveryType type);

}
