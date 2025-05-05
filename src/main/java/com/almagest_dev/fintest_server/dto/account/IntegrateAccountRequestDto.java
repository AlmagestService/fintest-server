package com.almagest_dev.fintest_server.dto.account;

/*통합계좌조회 요청*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrateAccountRequestDto {
    // Header: Authorization - Bearer 토큰 ( ApiKey )
    // 테스트베드로부터 전송받은 ApiKey를 HTTP Header에 추가
    // Authorization Bearer {ApiKey};

    // 사용자 정보 확인으로 수신한 인증 코드 - 금융식별자
    // 필수: N
    // 있으면 조회, 없으면 식별자와 데이터 새로 생성
    private String userFinanceId;

    // 요청 일시
    private String requestDate;

    // 요청 사용자 이름
    private String userName;

    private String serviceName;

    // 금융기관 업권 구분
    // 예시 값: "1" (1: 은행) - 고정값
    private String inquiryBankType;

    // 지정 번호
    // 예시 값: "1" - 고정값
    // 필수: Y
    // 타입: N(6)
    private String traceNo;

    // 조회 건수
    // 예시 값: "30" - 고정값
    // 필수: Y
    // 타입: N(6)
    private String inquiryRecordCnt;
}
