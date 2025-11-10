package com.hubEleven.deliveryManager.infrastructure;

import com.hubEleven.deliveryManager.domain.DeliveryManager;
import com.hubEleven.deliveryManager.domain.DeliveryManagerRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class DeliveryManagerRepositoryImpl implements DeliveryManagerRepository {

	private final DeliveryManagerJpaRepository deliveryManagerJpaRepository;

	public DeliveryManagerRepositoryImpl(DeliveryManagerJpaRepository deliveryManagerJpaRepository) {
		this.deliveryManagerJpaRepository = deliveryManagerJpaRepository;
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
	public Page<DeliveryManager> findAll(Pageable pageable) {
		return deliveryManagerJpaRepository.findAll(pageable);
	}

	@Override
	public Integer findMaxDeliveryOrderByHubId(UUID hubId) {
		return deliveryManagerJpaRepository.findMaxDeliveryOrderByHubId(hubId);
	}

	@Override
	public Optional<DeliveryManager>
			findFirstByHubIdIsNullOrderByLastDeliveryTimeAscDeliveryOrderAsc() {
		return deliveryManagerJpaRepository
				.findFirstByHubIdIsNullOrderByLastDeliveryTimeAscDeliveryOrderAsc();
	}

	@Override
	public Optional<DeliveryManager> findFirstByHubIdOrderByLastDeliveryTimeAscDeliveryOrderAsc(
			UUID hubId) {
		return deliveryManagerJpaRepository.findFirstByHubIdOrderByLastDeliveryTimeAscDeliveryOrderAsc(
				hubId);
	}

    @Override
    public boolean existsByHubIdAndDeliveryManagerId(UUID hubId, Long managerId) {
        return deliveryManagerJpaRepository.existsByHubIdAndDeliveryManagerId(hubId,managerId);
    }
}
