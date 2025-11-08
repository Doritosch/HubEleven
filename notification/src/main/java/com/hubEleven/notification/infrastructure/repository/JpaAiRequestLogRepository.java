package com.hubEleven.notification.infrastructure.repository;

import com.hubEleven.notification.domain.model.AiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAiRequestLogRepository extends JpaRepository<AiRequestLog, UUID> {
    boolean existsByOrderId(UUID orderId);
    Optional<AiRequestLog> findByOrderId(UUID orderId);
}
