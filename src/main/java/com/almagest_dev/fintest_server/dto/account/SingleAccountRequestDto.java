package com.almagest_dev.fintest_server.dto.account;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleAccountRequestDto {
    // Header: Authorization - Bearer 토큰 ( ApiKey )
    // 테스트베드로부터 전송받은 ApiKey를 HTTP Header에 추가
    // Authorization Bearer {ApiKey};

    // 사용자 정보 확인으로 수신한 인증 코드
    // 필수: N
    // 있으면 조회, 없으면 식별자와 데이터 새로 생성
    private String userFinanceId;

    //조회 게좌 식별자
    private String fintechUseNum;

    //요청일시 - "20160310101921"
    private String tranDtime;

}
