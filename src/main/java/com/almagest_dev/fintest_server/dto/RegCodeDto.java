package com.almagest_dev.fintest_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 기관 코드 정보를 담는 DTO 클래스
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegCodeDto {
    // 기관 코드
    private String orgCode;
    // 기관명
    private String orgName;
    // 생성할 등록 코드 개수
    private String size;
    // 기관별 계좌번호 길이
    private String accountLength;
}
