package com.almagest_dev.fintest_server.dto.recipient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*수취인조회 응답*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipientInquiryResponseDto {

     //==============================수취정보

    // 입금 기관 코드
    private String bankCodeStd;

    // 입금 기관명
    // 예시 값: "오픈은행"
    private String bankName;

    // 입금 계좌 번호
    // 예시 값: "3001230000678"
    private String accountNum;

    // 입금 계좌 번호 (출력용)
    // 예시 값: "300123-0000-***"
    // 타입: NS*(20)
    private String accountNumMasked;

    // 입금 계좌 인자 내역 - 수취인 계좌에 표시될 정보
    // 예시 값: "홍길동송금"
    // 타입: AH(20)
    private String printContent;

    // 수취인 성명
    // 예시 값: "허균(개인)"
    private String accountHolderName;

    // 수취 조회 계좌 고유 번호 - 계좌 UUID
    // 필수: Y
    // 타입: AN(40)
    private String recvAccountFintechUseNum;


    //===============================송금정보

    // 출금 (개설) 기관 표준 코드
    private String wdBankCodeStd;

    // 출금 (개설) 기관명
    // 예시 값: "오픈은행"
    // 타입: AH(20)
    private String wdBankName;

    // 출금 계좌 번호
    // 예시 값: "1101230000678"
    // 타입: AN(16)
    private String wdAccountNum;

    // 거래 금액
    // 예시 값: "10000"
    // 타입: N(12)
    private String tranAmt;
}
