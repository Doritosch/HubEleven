package com.hubEleven.user.application;

import static com.hubEleven.user.domain.exception.UserErrorCode.FAILED_LOGIN;
import static com.hubEleven.user.domain.exception.UserErrorCode.NOT_APPROVED_USER;

import com.commonLib.common.exception.GlobalException;
import com.hubEleven.user.application.command.LoginCommand;
import com.hubEleven.user.application.dto.TokenResult;
import com.hubEleven.user.domain.exception.UserErrorCode;
import com.hubEleven.user.domain.model.User;
import com.hubEleven.user.domain.repository.UserRepository;
import com.hubEleven.user.infrastructure.token.RefreshToken;
import com.hubEleven.user.infrastructure.token.RefreshTokenRepository;
import com.hubEleven.user.domain.vo.SignStatus;
import com.hubEleven.user.infrastructure.security.CustomUserDetails;
import com.hubEleven.user.infrastructure.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authManager;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public TokenResult login(LoginCommand command) {
		try {
			Authentication authentication =
					authManager.authenticate(
							new UsernamePasswordAuthenticationToken(command.username(), command.password()));

			CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

			// 승인된 사용자만 로그인 가능
			if (user.getUser().getStatus() != SignStatus.APPROVED) {
				throw new GlobalException(NOT_APPROVED_USER);
			}

			String accessToken = jwtProvider.generateToken(user);
			String refreshToken = UUID.randomUUID().toString();
			RefreshToken token = new RefreshToken(refreshToken, user.getUserId());

			refreshTokenRepository.save(token);
			return new TokenResult(accessToken, refreshToken);
		} catch (AuthenticationException e) {
			throw new GlobalException(FAILED_LOGIN);
		}
	}

	public TokenResult refresh(String refreshToken) {
		RefreshToken token = refreshTokenRepository.findById(refreshToken)
				.orElseThrow(() ->
					new GlobalException(UserErrorCode.INVALID_REFRESH_TOKEN)
				);

		User user = userRepository.findByIdAndNotDeleted(token.getUserId())
				.orElseThrow(() -> new GlobalException(UserErrorCode.NOT_FOUND_USER));

		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		String createdAccessToken = jwtProvider.generateToken(customUserDetails);
		String createdRefreshToken = UUID.randomUUID().toString();

		refreshTokenRepository.delete(token);
		refreshTokenRepository.save(new RefreshToken(createdRefreshToken, user.getId()));

		return new TokenResult(createdAccessToken, createdRefreshToken);
	}
}
