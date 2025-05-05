package com.almagest_dev.fintest_server.dto.deposit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDepositResponseDto {

    // 거래 금액
    // 예시 값: "10000"
    // 타입: N(12)
    private String tranAmt;

    // 입금 건수
    // 예시 값: "1" - 고정
    // 타입: N(5)
    private int resCnt;

    //=================================송금정보

    // 출금 기관 표준 코드
    // 예시 값: "097"
    // 타입: AN(3)
    private String wdBankCodeStd;

    // 출금 기관명
    // 예시 값: "오픈은행"
    // 타입: AH(20)
    private String wdBankName;

    // 출금 계좌 번호 (출력용)
    // 예시 값: "000-1230000-***"
    // 타입: NS*(20)
    private String wdAccountNumMasked;

    // 출금 계좌 인자 내역
    // 예시 값: "환불금액"
    // 타입: AH(20)
    private String wdPrintContent;

    // 송금인 성명
    // 예시 값: "허균"
    // 타입: AH(20)
    private String wdAccountHolderName;


    //=================================수신정보

    // 입금계좌 핀테크 이용 번호 - UUID
    // 타입: AN(40)
    private String fintechUseNum;

    // 계좌 별명 (Alias)
    // 예시 값: "급여계좌"
    // 타입: AH(50)
    private String accountAlias;

    // 입금 기관 표준 코드
    // 예시 값: "097"
    // 타입: AN(3)
    private String bankCodeStd;

    // 입금 기관명
    // 예시 값: "오픈은행"
    // 타입: AH(20)
    private String bankName;

    // 입금 계좌 번호 (출력용)
    // 예시 값: "000-1230000-***"
    // 타입: NS*(20)
    private String accountNumMasked;

    // 입금 계좌 인자 내역
    // 예시 값: "쇼핑몰환불"
    // 타입: AH(20)
    private String printContent;

    // 수취인 성명
    // 예시 값: "홍길동"
    // 타입: AH(20)
    private String accountHolderName;
}
