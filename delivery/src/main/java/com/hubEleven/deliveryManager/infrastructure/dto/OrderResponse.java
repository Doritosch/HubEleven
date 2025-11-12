package com.hubEleven.deliveryManager.infrastructure.dto;

import java.util.UUID;

public record OrderResponse(UUID oderId,
                            UUID requestorCompanyId,
                            UUID recipientCompanyId,
                            UUID productId,
                            UUID deliveryId,
                            Long quantity,
                            String note)  {

}
