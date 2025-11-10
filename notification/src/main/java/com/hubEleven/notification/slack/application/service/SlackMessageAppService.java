package com.hubEleven.notification.slack.application.service;

import com.hubEleven.common.exception.GlobalException;
import com.hubEleven.common.request.CommonPageRequest;
import com.hubEleven.common.response.CommonPageResponse;
import com.hubEleven.common.utils.PagingUtils;
import com.hubEleven.notification.ai.application.dto.MessageGenerationRequest;
import com.hubEleven.notification.ai.application.dto.MessageGenerationResponse;
import com.hubEleven.notification.ai.application.service.AiAppService;
import com.hubEleven.notification.ai.presentation.request.MessageGenerateRequest;
import com.hubEleven.notification.slack.application.dto.SlackMessageCreateRequest;
import com.hubEleven.notification.slack.application.dto.SlackMessageResponse;
import com.hubEleven.notification.slack.application.dto.SlackMessageUpdateRequest;
import com.hubEleven.notification.slack.domain.exception.SlackMessageErrorCode;
import com.hubEleven.notification.slack.domain.model.SlackMessage;
import com.hubEleven.notification.slack.domain.model.SlackMessageStatus;
import com.hubEleven.notification.slack.domain.repository.SlackMessageRepository;
import com.hubEleven.notification.slack.domain.service.SlackMessageDomainService;
import com.hubEleven.notification.slack.infrastructure.client.SlackWebhookClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackMessageAppService {

    private final SlackMessageRepository slackMessageRepository;
    private final SlackWebhookClient slackWebhookClient;
    private final AiAppService aiAppService;
    private final SlackMessageDomainService slackMessageDomainService;

    @Transactional
    public SlackMessageResponse createMessage(SlackMessageCreateRequest request) {

        slackMessageRepository.findFirstByOrderIdAndStatus(request.orderId(), SlackMessageStatus.SENT)
                .ifPresent(m -> { throw new GlobalException(SlackMessageErrorCode.SLACK_MESSAGE_ALREADY_SENT); });

        Long currentUserId =  getCurrentUserId();

        MessageGenerationRequest aiRequest = convertToAiRequest(request);
        MessageGenerationResponse aiResponse = aiAppService.generateDispatchGuidance(aiRequest);

        String formattedMessage = slackMessageDomainService.formatMessage(request, aiResponse);

        SlackMessage slackMessage = SlackMessage.create(
                request.orderId(),
                request.recipientId(),
                request.channel(),
                formattedMessage
        );

        SlackMessage savedSlackMessage = slackMessageRepository.save(slackMessage);

        sendToSlackAsync(
                savedSlackMessage.getId(),
                formattedMessage
        );

        return SlackMessageResponse.from(savedSlackMessage);
    }

    @Transactional
    public SlackMessageResponse updateMessage(UUID messageId, SlackMessageUpdateRequest request){
        SlackMessage slackMessage = slackMessageRepository.findById(messageId)
                .orElseThrow(()-> new GlobalException(SlackMessageErrorCode.SLACK_MESSAGE_NOT_FOUND));

        slackMessage.updateMessage(request.message());
        SlackMessage updatedSlackMessage = slackMessageRepository.save(slackMessage);

        return SlackMessageResponse.from(updatedSlackMessage);
    }

    @Transactional
    public void deleteMessage(UUID messageId){
        Long currentUserId = getCurrentUserId();

        SlackMessage slackMessage = slackMessageRepository.findById(messageId)
                .orElseThrow(()-> new GlobalException(SlackMessageErrorCode.SLACK_MESSAGE_NOT_FOUND));

        slackMessage.delete(currentUserId);
        slackMessageRepository.save(slackMessage);
    }

    public SlackMessageResponse getMessage(UUID messageId) {
        SlackMessage slackMessage = slackMessageRepository.findById(messageId)
                .orElseThrow(() -> new GlobalException(SlackMessageErrorCode.SLACK_MESSAGE_NOT_FOUND));

        return SlackMessageResponse.from(slackMessage);
    }

    public CommonPageResponse<SlackMessageResponse> searchMessages(
            SlackMessageStatus status,
            String channel,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            CommonPageRequest pageReq) {

        var page = slackMessageRepository.search(
                status,
                channel,
                dateFrom,
                dateTo,
                pageReq.toPageable());

        return PagingUtils.convert(page, SlackMessageResponse::from);
    }

    private MessageGenerationRequest convertToAiRequest(SlackMessageCreateRequest request) {
        return MessageGenerationRequest.of(
                request.orderId(),
                request.customerName(),
                request.customerEmail(),
                request.orderDateTime(),
                request.requestedArrivalDateTime(),
                request.sourceHub(),
                request.viaHubs(),
                request.destinationHub(),
                request.destinationAddress(),
                request.requestNote(),
                request.deliveryManagerName(),
                request.deliveryManagerEmail(),
                request.items().stream()
                        .map(item -> MessageGenerationRequest.Item.of(
                                item.name(),
                                item.quantity(),
                                item.note()))
                        .toList()
        );
    }

    private void sendToSlackAsync(UUID messageId, String messageText) {
        String title = "배송 예상 시간 알림";

        slackWebhookClient.sendMessage(title, messageText)
                .subscribe(ok -> {
                            if (Boolean.TRUE.equals(ok)) {
                                updateMessageStatus(messageId, SlackMessageStatus.SENT);
                                log.info("Slack message sent. id={}", messageId);
                            } else {
                                updateMessageStatus(messageId, SlackMessageStatus.FAILED);
                                log.error("Slack webhook returned non-ok for id={}", messageId);
                            }
                        },
                        error -> {
                            updateMessageStatus(messageId, SlackMessageStatus.FAILED);
                            log.error("Failed to send Slack message. id={}", messageId, error);
                        }
                );
    }

    @Transactional
    protected void updateMessageStatus(UUID messageId, SlackMessageStatus status) {
        slackMessageRepository.findById(messageId)
                .ifPresent(message -> {
                    if (status == SlackMessageStatus.SENT) {
                        message.markAsSent();
                    } else if (status == SlackMessageStatus.FAILED) {
                        message.markAsFailed();
                    }
                    slackMessageRepository.save(message);
                });
    }

    //TODO: UserId 가지고 오기
    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long userId) {
            return userId;
        }
        return null;
    }
}
