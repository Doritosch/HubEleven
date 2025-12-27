package com.hubEleven.notification.ai.infrastructure.repository;

import com.hubEleven.notification.ai.domain.entity.AiRequestLog;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAiRequestLogRepository extends JpaRepository<AiRequestLog, UUID> {
	boolean existsByOrderId(UUID orderId);

	Optional<AiRequestLog> findByOrderId(UUID orderId);
}
