package com.hubEleven.user.presentation.dto.response;

import com.hubEleven.user.application.dto.TokenResult;

public record TokenResponse(String accessToken, String refreshToken) {
	public static TokenResponse from(TokenResult result) {
		return new TokenResponse(result.accessToken(), result.refreshToken());
	}
}
