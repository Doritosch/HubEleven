package com.hubEleven.deliveryManager.infrastructure;

import com.hubEleven.deliveryManager.domain.DeliveryManager;
import com.hubEleven.deliveryManager.domain.DeliveryManagerRepository;
import com.hubEleven.deliveryManager.domain.DeliveryType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class DeliveryManagerRepositoryImpl implements DeliveryManagerRepository {

	private final DeliveryManagerJpaRepository deliveryManagerJpaRepository;

	public DeliveryManagerRepositoryImpl(DeliveryManagerJpaRepository deliveryManagerJpaRepository) {
		this.deliveryManagerJpaRepository = deliveryManagerJpaRepository;
	}

	@Override
	public Integer findMaxDeliveryOrderByDeliveryType(DeliveryType deliveryType) {
		return deliveryManagerJpaRepository.findMaxDeliveryOrderByDeliveryType(deliveryType);
	}

	@Override
	public DeliveryManager save(DeliveryManager deliveryManager) {
		return deliveryManagerJpaRepository.save(deliveryManager);
	}

	@Override
	public Optional<DeliveryManager> findById(Long id) {
		return deliveryManagerJpaRepository.findById(id);
	}

	@Override
	public List<DeliveryManager> findAll() {
		return deliveryManagerJpaRepository.findAll();
	}

	@Override
	public List<DeliveryManager> findAllByHubId(UUID hubId) {
		return deliveryManagerJpaRepository.findByHubId(hubId);
	}

	@Override
	public Integer findMaxDeliveryOrderByHubId(UUID hubId) {
		return deliveryManagerJpaRepository.findMaxDeliveryOrderByHubId(hubId);
	}
}
