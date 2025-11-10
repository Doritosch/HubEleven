package com.hubEleven.notification.slack.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	SecurityFilterChain security(HttpSecurity http, JwtAuthenticationConverter jwtAuthConverter)
			throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth ->
								auth.requestMatchers(
												"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/health")
										.permitAll()
										.anyRequest()
										.authenticated())
				.oauth2ResourceServer(
						oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
				.build();
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter conv = new JwtGrantedAuthoritiesConverter();
		conv.setAuthoritiesClaimName("role");
		conv.setAuthorityPrefix("");
		var c = new JwtAuthenticationConverter();
		c.setJwtGrantedAuthoritiesConverter(conv);
		return c;
	}
}
