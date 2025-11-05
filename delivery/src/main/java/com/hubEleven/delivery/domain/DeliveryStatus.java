package com.hubEleven.delivery.domain;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    HUB_WAITHING("허브이동대기중"),
    HUB_DELIVERING("허브이동중"),
    HUB_ARRIVED("허브도착"),
    DELIVERING("배송중");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }
}
