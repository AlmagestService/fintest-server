package com.almagest_dev.fintest_server.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListResponseDto {

    // 기관 코드
    // 예시 값: "098"
    // 타입: AN(3)
    private String bankCodeTran;

    // 개설 기관명
    // 예시 값: "오픈은행"
    // 타입: AH(20)
    private String bankName;

    // 핀테크 이용 번호 - 계좌식별 UUID
    // 타입: AN(40)
    private String fintechUseNum;

    // 예시 값: "1000000"
    // 타입: SN(2)
    private String balanceAmt;

    // 조회된 거래 내역 목록
    private List<TransactionDetailDto> resList;


}
