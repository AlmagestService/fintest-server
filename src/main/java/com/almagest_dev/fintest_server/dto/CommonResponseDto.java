package com.almagest_dev.fintest_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 공통 API 응답 DTO 클래스
 * API 응답의 표준 구조를 정의합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseDto<T> {

    // 거래 고유 번호 (API) UUID
    // 타입: aNS(40)
    private String apiTranId;

    // 거래 일시 (ms)
    // 예시 값: "20190910101921567"
    // 타입: N(17)
    private String apiTranDtm;

    // 응답 코드
    // 예시 값: "A0000"
    private String rspCode;

    // 응답 메시지 (API)
    // 타입: AH(300)
    private String rspMessage;

    // 요청별 Dto
    private T data;

    // 응답 코드 반환
    public String getRspCode() {
        return rspCode;
    }

    // 응답 코드 설정
    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    // 응답 메시지 반환
    public String getRspMessage() {
        return rspMessage;
    }

    // 응답 메시지 설정
    public void setRspMessage(String rspMessage) {
        this.rspMessage = rspMessage;
    }

    // 데이터 반환
    public T getData() {
        return data;
    }

    // 데이터 설정
    public void setData(T data) {
        this.data = data;
    }
}

