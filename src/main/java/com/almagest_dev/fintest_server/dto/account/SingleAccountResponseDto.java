package com.almagest_dev.fintest_server.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleAccountResponseDto {

    // 응답코드를 부여한 참가기관 표준코드
    private String bankCodeTran; // Example: "098"

    // 개설기관명
    private String bankName; // Example: "오픈은행"

    // 계좌식별자
    private String fintechUseNum; //

    // 계좌잔액
    private String balanceAmt; // Example: "1000000"

    // 출금가능금액
    private String availableAmt; // Example: "1000000"

    // 계좌종류 (1: 수시입출금, 2: 예적금, 6: 수익증권)
    private String accountType; // Example: "1"

    // 상품명
    private String productName; // Example: "내맘대로통장"

    // 계좌개설일
    private String accountIssueDate; // Example: "20190110"

    // 최종거래일
    private String lastTranDate; // Example: "20191010"

    //계좌 정보
    private AccountInfoDto accountInfo;
}
