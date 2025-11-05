package com.hubEleven.hub.application.dto;

import java.util.UUID;

public record HubResult(
		UUID hubId,
		String name,
		String address,
		Double latitude,
		Double longitude,
		String regionCode) {}
