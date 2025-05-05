package com.almagest_dev.fintest_server.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailDto {


    //거래 식별 시퀀스 번호
    private String tranNum;

    // 거래 일자
    // 예시 값: "20160310"
    // 타입: N(8)
    private String tranDate;

    // 거래 시간
    // 예시 값: "113000"
    // 타입: N(6)
    private String tranTime;

    // 입출금 구분 ("입금" 또는 "출금")
    // 예시 값: "입금"
    // 타입: AH(8)
    private String inoutType;

    // 거래 구분 (예: 현금, 대체, 급여, 타행환, F/B출금 등)
    // 예시 값: "현금" - 현재 현금 고정
    // 타입: AH(10)
    private String tranType;

    // 통장 인자 내용
    // 예시 값: "통장인자내용"
    // 타입: AH(20)
    private String printContent;

    // 거래 금액
    // 예시 값: "450000"
    // 타입: N(12)
    private String tranAmt;

    // 타입: SN(13)
    private String afterBalanceAmt;
}
