package com.almagest_dev.fintest_server.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyDto {
    // API 키 값
    private String apiKey;
    // API 키 만료일
    private String expiredDate;
    // API 키 사용 가능 횟수
    private int apiCallCount;
}
