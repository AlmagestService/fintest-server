package com.almagest_dev.fintest_server.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListRequestDto {
    // Header: Authorization - Bearer 토큰 ( ApiKey )
    // 테스트베드로부터 전송받은 ApiKey를 HTTP Header에 추가
    // Authorization Bearer {ApiKey};


    //계좌 핀테크 이용 번호 - UUID(서비스간 계좌식별자)
    // 필수: Y
    private String fintechUseNum;

    // 조회 구분 코드
    // A: All, I: 입금, O: 출금 - 기본값 A
    // 필수: N
    private String inquiryType;

    // 조회 기준 코드
    // D: 일자, T: 시간 - 기본값 D
    // 필수: N
    private String inquiryBase;

    // 조회 시작 일자
    // 예시 값: "20160404"
    // 필수: Y
    private String fromDate;

    // 조회 시작 시간
    // 예시 값: "100000" - 10:00:00 - 기본값 00:00:00
    // 필수: N
    private String fromTime;

    // 조회 종료 일자
    // 예시 값: "20160405"
    // 필수: Y
    private String toDate;

    // 조회 종료 시간
    // 예시 값: "110000" - 11:00:00 - 기본값 24:00:00
    // 필수: Y
    private String toTime;

    // 정렬 순서
    // D: Descending, A: Ascending - 기본값 D
    // 필수: N
    private String sortOrder;

    // 요청 일시
    // 예시 값: "20160310101921"
    // 필수: Y
    private String tranDtime;

    // 요청 조회수
    private String dataLength;
}
