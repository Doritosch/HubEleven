package com.hubEleven.delivery.deliveryManager;

import com.hubEleven.deliveryManager.application.DeliveryManagerService;
import com.hubEleven.deliveryManager.presentation.dto.request.DeliveryManagerCreateRequestDto;
import com.hubEleven.deliveryManager.presentation.dto.response.DeliveryManagerResponseDto;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class DeliveryManagerCreateTest {
    @Autowired
    private DeliveryManagerService deliveryManagerService;



    @Test
    void testConcurrentDeliveryOrderCreation() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        Set<Integer> deliveryOrders = new HashSet<>();

        for (int i = 0; i < threadCount; i++) {
            long managerId = 1000 + i; // 직접 managerId 지정
            executor.submit(() -> {
                try {
                    DeliveryManagerCreateRequestDto dto =
                        new DeliveryManagerCreateRequestDto(managerId); // 필요 필드만 넣음
                    DeliveryManagerResponseDto response = deliveryManagerService.createDeliveryManager(dto);

                    synchronized (deliveryOrders) {
                        if (deliveryOrders.contains(response.deliveryOrder())) {
                            System.err.println("중복된 배송순번 발생: " + response.deliveryOrder());
                        } else {
                            deliveryOrders.add(response.deliveryOrder());
                            System.out.println("생성된 배송순번: " + response.deliveryOrder());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("생성 실패: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("테스트 종료, 생성된 순번 수: " + deliveryOrders.size());
    }
}
