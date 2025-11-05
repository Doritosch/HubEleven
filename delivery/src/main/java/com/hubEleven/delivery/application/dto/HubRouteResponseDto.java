package com.hubEleven.delivery.application.dto;

import java.util.UUID;

public record HubRouteResponseDto(
        UUID hubRouteId,
        UUID fromHubId,
        UUID toHubId,
        Double distance,
        Long duration
) {
}
