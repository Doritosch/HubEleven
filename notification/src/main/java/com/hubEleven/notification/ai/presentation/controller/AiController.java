package com.hubEleven.notification.ai.presentation.controller;

import com.hubEleven.common.response.ApiResponse;
import com.hubEleven.common.response.ApiResponseEntity;
import com.hubEleven.notification.ai.application.dto.MessageGenerationResponse;
import com.hubEleven.notification.ai.application.service.AiAppService;
import com.hubEleven.notification.ai.presentation.request.MessageGenerateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ai/dispatch")
public class AiController {

	private final AiAppService aiAppService;

	@PostMapping("/messages")
	public ResponseEntity<ApiResponse<MessageGenerationResponse>> generateMessage(
			@Valid @RequestBody MessageGenerateRequest request) {
		MessageGenerationResponse response = aiAppService.generateDispatchGuidance(request.toCommand());
		return ApiResponseEntity.success(response);
	}
}
