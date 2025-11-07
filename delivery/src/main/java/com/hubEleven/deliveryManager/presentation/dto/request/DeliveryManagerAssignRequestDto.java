package com.hubEleven.deliveryManager.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record DeliveryManagerAssignRequestDto(
    List<UUID> hubIds, //경유하는 허브들 ID 목록
    @NotNull UUID toHubId, // 목적지 허브 ID
    @NotNull UUID receivingCompanyId //수령업체 ID
		) {}
