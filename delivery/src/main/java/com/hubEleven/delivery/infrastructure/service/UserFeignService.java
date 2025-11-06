package com.hubEleven.delivery.infrastructure.service;

import com.hubEleven.delivery.infrastructure.client.UserFeignClient;
import com.hubEleven.delivery.infrastructure.dto.UserFeignResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserFeignService {
    private final UserFeignClient userFeignClient;

    public UserFeignResponseDto getUserInfo(UUID companyId){
        return userFeignClient.getUser(companyId, "companyManager");
    }
}