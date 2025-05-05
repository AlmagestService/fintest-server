package com.almagest_dev.fintest_server.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {
    // Header: Authorization - Bearer 토큰 ( ApiKey )
    // 테스트베드로부터 전송받은 ApiKey를 HTTP Header에 추가
    // Authorization Bearer {ApiKey};


    // ================================거래식별
    // 요청 고객 회원 번호 - UUID
    // 필수: Y
    private String userFinanceId;

    // 이체 용도
    // TR: 송금, ST: 결제, AU: 인증
    // 예시 값: "TR" - 고정값
    // 필수: Y
    private String transferPurpose;

    //==================================거래정보

    // 거래 금액
    // 예시 값: "10000"
    // 필수: Y
    // 타입: N(12)
    private String tranAmt;

    // 요청 일시
    // 예시 값: "20190910101921"
    // 필수: Y
    private String tranDtime;


    //==================================송금인정보


    // 출금 계좌 핀테크 이용 번호 - UUID
    // 필수: Y
    private String fintechUseNum;

    // 출금 계좌 인자 내역 - 송금인쪽 계좌에 표시
    // 예시 값: "오픈뱅킹출금"
    // 필수: N
    private String wdPrintContent;

    // 요청 고객 성명
    // 예시 값: "홍길동"
    // 필수: N
    private String reqClientName;

    // 요청 고객 계좌 개설 기관 코드
    // 예시 값: "097"
    // 필수: Y
    private String reqClientBankCode;

    // 요청 고객 계좌 번호
    // 예시 값: "1101230000678"
    // 필수: Y
    private String reqClientAccountNum;

    //===================================수취인정보

    // 수취 계좌 고유 번호 - 계좌 UUID
    // 필수: Y
    private String recvAccountFintechUseNum;

    // 최종 수취 고객 성명
    // 예시 값: "김오픈"
    // 필수: Y
    private String recvClientName;

    // 최종 수취 고객 계좌 개설 기관 코드
    // 예시 값: "097"
    // 필수: Y
    private String recvClientBankCode;

    // 최종 수취 고객 계좌 번호
    // 예시 값: "232000067812"
    // 필수: Y
    private String recvClientAccountNum;

    // 입금 계좌 인자 내역 - 수취인쪽 계좌에 표시
    // 필수: N
    private String dpsPrintContent;
}
