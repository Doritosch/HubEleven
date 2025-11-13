package com.hubEleven.deliveryManager.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	// 디버깅용: 요청 헤더 출력
	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			System.out.println("=== Feign Request ===");
			System.out.println("URL: " + requestTemplate.url());
			System.out.println("Method: " + requestTemplate.method());
			System.out.println("Headers: " + requestTemplate.headers());
			System.out.println("===================");
		};
	}
}
