package com.hubEleven.delivery.application.dto;

import java.util.UUID;

public record CompanyResponseDto(
        UUID companyId,
        UUID HubId
) {
}
