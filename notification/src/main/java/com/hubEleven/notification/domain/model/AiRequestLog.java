package com.hubEleven.notification.domain.model;

import com.hubEleven.common.annotation.SoftDeletable;
import com.hubEleven.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;


@Entity
@Getter
@Table(name = "p_ai_request_log")
@SoftDeletable
public class AiRequestLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_request_log_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus status;

    @Lob
    private String rawPrompt;

    @Lob
    private String rawResponse;

    protected AiRequestLog() {}

    private AiRequestLog(UUID orderId, RequestStatus status){
        this.orderId = orderId;
        this.status = status;
    }

    public static AiRequestLog requested(UUID orderId) {
        return new AiRequestLog(orderId, RequestStatus.REQUESTED);
    }

    public void success(String prompt, String response) {
        this.status = RequestStatus.SUCCESS;
        this.rawPrompt = prompt;
        this.rawResponse = response;
    }

    public void fail(String prompt, String response) {
        this.status = RequestStatus.FAIL;
        this.rawPrompt = prompt;
        this.rawResponse = response;
    }
}
