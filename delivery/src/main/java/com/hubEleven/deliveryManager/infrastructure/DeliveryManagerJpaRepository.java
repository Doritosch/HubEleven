package com.hubEleven.deliveryManager.infrastructure;

import com.hubEleven.deliveryManager.domain.DeliveryManager;
import com.hubEleven.deliveryManager.domain.DeliveryType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManager, Long> {

	@Query(
			"SELECT MAX(dm.deliveryOrder) FROM DeliveryManager dm WHERE dm.deliveryType = :deliveryType")
	Integer findMaxDeliveryOrderByDeliveryType(DeliveryType deliveryType);

	List<DeliveryManager> findByHubId(UUID hubId);

	@Query(
			"""
		SELECT MAX(dm.deliveryOrder)
		FROM DeliveryManager dm
		WHERE (:hubId IS NULL AND dm.hubId IS NULL)
			OR (:hubId IS NOT NULL AND dm.hubId = :hubId)
""")
	Integer findMaxDeliveryOrderByHubId(@Param("hubId") UUID hubId);
}
