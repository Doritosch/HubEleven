package com.hubEleven.notification.infrastructure.repository;

import com.hubEleven.notification.domain.model.AiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaAiRequestLogRepository extends JpaRepository<AiRequestLog, UUID> {
    List<AiRequestLog> findByOrderId(UUID orderId);
}
