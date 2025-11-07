package com.hubEleven.deliveryManager.infrastructure;

import com.hubEleven.deliveryManager.domain.DeliveryManager;
import com.hubEleven.deliveryManager.domain.DeliveryType;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT dm
        FROM DeliveryManager dm
        WHERE dm.deliveryType = :deliveryType
          AND (:hubId IS NULL OR dm.hubId = :hubId)
        ORDER BY
          CASE WHEN dm.lastDeliveryTime IS NULL THEN 0 ELSE 1 END ASC,
          dm.deliveryOrder ASC,
          dm.lastDeliveryTime ASC
    """)
    List<DeliveryManager> findNextAvailableManager(
        @Param("deliveryType") DeliveryType deliveryType,
        @Param("hubId") UUID hubId,
        Pageable pageable
    );

// COMPANY 타입 배송 담당자 최대 순번 조회 (허브별)
    @Query("SELECT MAX(dm.deliveryOrder) FROM DeliveryManager dm WHERE dm.deliveryType = :type AND dm.hubId = :hubId")
    Integer findMaxDeliveryOrderByHubAndType(@Param("hubId") UUID hubId, @Param("type") DeliveryType type);

    //1.3 오래된 순서대로 배송 담당자 조회 (HUB)
    List<DeliveryManager> findTop10ByDeliveryTypeOrderByLastDeliveryTimeAsc(DeliveryType type);


    //1.4 오래된 순서대로 배송 담당자 조회 (COMPANY)
    List<DeliveryManager> findTop10ByDeliveryTypeAndHubIdOrderByLastDeliveryTimeAsc(DeliveryType type, UUID hubId);

}
