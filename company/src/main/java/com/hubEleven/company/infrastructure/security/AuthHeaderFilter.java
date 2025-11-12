package com.hubEleven.company.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class AuthHeaderFilter extends OncePerRequestFilter {

	private static final String USER_ID_HEADER = "X-User-Id";
	private static final String USER_ROLE_HEADER = "X-User-Role";
	private static final String HUB_ID_HEADER = "X-Hub-Id";
	private static final String COMPANY_ID_HEADER = "X-Company-Id";

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		try {
			String userIdStr = request.getHeader(USER_ID_HEADER);
			String roleStr = request.getHeader(USER_ROLE_HEADER);
			String hubIdStr = request.getHeader(HUB_ID_HEADER);
			String companyIdStr = request.getHeader(COMPANY_ID_HEADER);

			if (StringUtils.hasText(userIdStr) && StringUtils.hasText(roleStr)) {
				try {
					Long userId = Long.parseLong(userIdStr.trim());
					Role role = Role.fromHeader(roleStr);
					UUID hubId = parseUuidOrNull(hubIdStr);
					UUID companyId = parseUuidOrNull(companyIdStr);

					AuthUserContext.set(new AuthUser(userId, role, hubId, companyId));
				} catch (IllegalArgumentException ex) {
					log.warn("인증 헤더 파싱 실패 - userId: {}, role: {}", userIdStr, roleStr, ex);
				}
			}

			chain.doFilter(request, response);
		} finally {
			AuthUserContext.clear();
		}
	}

	private UUID parseUuidOrNull(String raw) {
		if (!StringUtils.hasText(raw)) {
			return null;
		}
		try {
			return UUID.fromString(raw.trim());
		} catch (IllegalArgumentException ex) {
			log.warn("UUID 헤더 파싱 실패 - value: {}", raw, ex);
			return null;
		}
	}
}