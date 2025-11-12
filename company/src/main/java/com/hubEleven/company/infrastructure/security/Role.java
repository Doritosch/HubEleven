package com.hubEleven.company.infrastructure.security;

public enum Role {
	MASTER,
	HUB_MANAGER,
	DELIVERY_MANAGER,
	COMPANY_MANAGER;

	public static Role fromHeader(String raw) {
		if (raw == null) {
			throw new IllegalArgumentException("role header is null");
		}

		String normalized = raw.trim().toUpperCase();
		if (normalized.startsWith("ROLE_")) {
			normalized = normalized.substring("ROLE_".length());
		}
		return Role.valueOf(normalized);
	}
}
