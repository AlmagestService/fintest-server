package com.almagest_dev.fintest_server.dto.deposit;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDepositRequestDto {
    // Header: Authorization - Bearer 토큰 ( ApiKey )
    // 테스트베드로부터 전송받은 ApiKey를 HTTP Header에 추가
    // Authorization Bearer {ApiKey};
    //==================================거래정보

    // 수취인 성명 검증 여부
    // 예시 값: "on" 또는 "off"
    // 필수: Y
    // 타입: aN(3)
    private String nameCheckOption;

    // 요청 일시
    // 예시 값: "20190910101921"
    // 필수: Y
    // 타입: N(14)
    private String tranDtime;

    // 입금 요청 건수 (고정값: 1)
    // 예시 값: "1"
    // 필수: Y
    // 타입: N(5)
    private int reqCnt = 1;

    // 거래 금액
    // 예시 값: "10000"
    // 필수: Y
    // 타입: N(12)
    private String tranAmt;

    // 이체 용도
    // 예시 값: "TR"
    // 필수: Y
    // 타입: AN(2)
    private String transferPurpose;

    //==================================송금정보

    // 출금 계좌 인자 내역
    // 예시 값: "환불금액"
    // 필수: N
    // 타입: AH(20)
    private String wdPrintContent;

    // 요청 고객 성명
    // 예시 값: "홍길동"
    // 필수: Y
    // 타입: AH(20)
    private String reqClientName;

    // 요청 고객 계좌 개설 기관 코드
    // 예시 값: "097"
    // 필수: N
    // 타입: AN(3)
    private String reqClientBankCode;

    // 요청 고객 계좌 번호
    // 예시 값: "1101230000678"
    // 필수: N
    // 타입: AN(16)
    private String reqClientAccountNum;

    // 요청 계좌 핀테크 이용 번호 - UUID
    // 필수: N
    // 타입: AN(40)
    private String reqAccountFintechUseNum;

    // 요청 고객 회원 번호 - UUID
    // 필수: Y
    // 타입: AN(40)
    private String reqClientNum;


    //==================================수신정보

    // 입금 계좌 인자 내역
    // 예시 값: "쇼핑몰환불"
    // 필수: Y
    // 타입: AH(20)
    private String printContent;

    // 수취 조회 계좌 고유 번호 - 계좌 UUID
    // 필수: Y
    // 타입: AN(40)
    private String recvAccountFintechUseNum;

}
