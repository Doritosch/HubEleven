package com.hubEleven.notification.domain.repository;

import com.hubEleven.notification.domain.model.AiRequestLog;

import java.util.Optional;
import java.util.UUID;

public interface AiRequestLogRepository {
    AiRequestLog save(AiRequestLog aiRequestLog);
    Optional<AiRequestLog> findById(UUID id);
}
