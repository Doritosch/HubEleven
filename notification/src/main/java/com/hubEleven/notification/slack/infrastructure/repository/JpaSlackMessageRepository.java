package com.hubEleven.notification.slack.infrastructure.repository;

import com.hubEleven.notification.slack.domain.model.SlackMessage;
import com.hubEleven.notification.slack.domain.model.SlackMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaSlackMessageRepository extends JpaRepository<SlackMessage, UUID> {
    Optional<SlackMessage> findFirstByOrderIdAndStatus(UUID orderId, SlackMessageStatus status);
}
