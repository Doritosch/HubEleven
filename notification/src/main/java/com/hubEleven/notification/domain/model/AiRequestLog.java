package com.hubEleven.notification.domain.model;

import com.hubEleven.common.annotation.SoftDeletable;
import com.hubEleven.common.model.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_ai_request_log")
@SoftDeletable
public class AiRequestLog extends BaseEntity {

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

}
