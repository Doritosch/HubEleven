package com.hubEleven.deliveryManager.infrastructure.dto;

import java.util.UUID;

public record UserInfoResponse(
    Long userId,
    String username,
    String name,
    String slackId,
    String phoneNumber,
    String role,
    String status,
    UUID companyId) {

}
