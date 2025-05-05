package com.almagest_dev.fintest_server.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {


    //================================거래정보

    // 거래 금액
    // 예시 값: "10000"
    // 타입: N(12)
    private String tranAmt;

    // 출금 한도 잔여 금액
    // 예시 값: "9990000"
    // 타입: N(12)
    private String wdLimitRemainAmt;

    //거래 결과
    // 예시 값: R, S, F
    private String tranResult;

    //=================================송금인정보

    // 출금 계좌 핀테크 이용 번호 - UUID
    private String fintechUseNum;

    // 출금 계좌 별명 (Alias)
    // 예시 값: "급여계좌"
    // 타입: AH(50)
    private String accountAlias;

    // 출금 기관 표준 코드
    // 예시 값: "097"
    // 타입: AN(3)
    private String bankCodeStd;

    // 출금 기관명
    // 예시 값: "오픈은행"
    // 타입: AH(20)
    private String bankName;

    // 출금 계좌 번호 (출력용)
    // 예시 값: "000-1230000-***"
    // 타입: NS*(20)
    private String accountNumMasked;

    // 출금 계좌 인자 내역
    // 예시 값: "출금계좌인자내역"
    // 타입: AH(20)
    private String printContent;

    // 송금인 성명
    // 예시 값: "홍길동"
    // 타입: AH(20)
    private String accountHolderName;


    //==================================수취인정보

    // 수취인 성명
    // 예시 값: "허균"
    // 타입: AH(20)
    private String dpsAccountHolderName;

    // 입금 기관 코드
    // 예시 값: "097"
    // 타입: AN(3)
    private String dpsBankCodeStd;

    // 입금 기관명
    // 예시 값: "오픈은행"
    // 타입: AH(20)
    private String dpsBankName;

    // 입금 계좌 번호 (출력용)
    // 예시 값: "000-1230000-***"
    // 타입: NS*(20)
    private String dpsAccountNumMasked;

    // 입금 계좌 인자 내역
    // 예시 값: "입금계좌인자내역" - 송금이 보내는 설명
    // 타입: AH(20)
    private String dpsPrintContent;

}
