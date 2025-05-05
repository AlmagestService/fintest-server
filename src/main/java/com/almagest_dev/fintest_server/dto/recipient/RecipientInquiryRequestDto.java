package com.almagest_dev.fintest_server.dto.recipient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*수취인조회 요청*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipientInquiryRequestDto {
    // Header: Authorization - Bearer 토큰 ( ApiKey )
    // 테스트베드로부터 전송받은 ApiKey를 HTTP Header에 추가
    // Authorization Bearer {ApiKey};


    // 요청 고객 회원 번호 - UUID
    // 필수: Y
    private String reqUserFinanceId;


    //요청계좌 식별자
    private String reqFintechUseNum;

    // 입금 은행 코드
    // 예시 값: "098"
    // 필수: Y
    private String bankCodeStd;

    // 계좌 번호
    // 예시 값: "3001230000678"
    // 필수: Y
    // 타입: AN(16)
    private String accountNum;

    // 입금 계좌 인자 내역
    // 예시 값: "홍길동송금"
    // 필수: N
    // 타입: AH(20)
    private String printContent;

    // 거래 금액
    // 예시 값: "10000"
    // 필수: Y
    // 타입: N(12)
    private String tranAmt;

    // 요청 고객 성명
    // 예시 값: "홍길동"
    // 필수: Y
    // 타입: AH(20)
    private String reqClientName;
}