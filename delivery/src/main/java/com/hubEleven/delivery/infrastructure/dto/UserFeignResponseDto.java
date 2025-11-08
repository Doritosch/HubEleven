package com.hubEleven.delivery.infrastructure.dto;

import java.util.UUID;

public record UserFeignResponseDto(UUID userId, String name, String slackId) {}
