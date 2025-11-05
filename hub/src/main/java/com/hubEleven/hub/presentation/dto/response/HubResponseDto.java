package com.hubEleven.hub.presentation.dto.response;

import java.util.UUID;

public record HubResponseDto(
		UUID hubId,
		String name,
		String address,
		Double latitude,
		Double longitude,
		String regionCode) {}
