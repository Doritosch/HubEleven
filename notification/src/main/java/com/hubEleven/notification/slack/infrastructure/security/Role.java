package com.hubEleven.notification.slack.infrastructure.security;

import com.commonLib.common.exception.GlobalException;
import com.hubEleven.notification.slack.domain.exception.SlackMessageErrorCode;

public enum Role {
	MASTER,
	HUB_MANAGER,
	DELIVERY_MANAGER,
	COMPANY_MANAGER;

	public static Role fromHeader(String raw) {
		if (raw == null) {
			throw new GlobalException(SlackMessageErrorCode.UNAUTHORIZED);
		}
		String normalized = raw.trim().toUpperCase();
		if (normalized.startsWith("ROLE_")) {
			normalized = normalized.substring("ROLE_".length());
		}
		return Role.valueOf(normalized);
	}
}
