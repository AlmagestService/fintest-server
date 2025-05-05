package com.almagest_dev.fintest_server.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*통합계좌조회 응답*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrateAccountResponseDto {


    //===================================요청식별

    // 사용자 정보 확인으로 수신또는 생성된 인증 코드
    private String userFinanceId;

    // 거래 일자 (계좌통합)
    // 예시 값: "20190910"
    // 타입: N(8)
    private String ainfoTranDate;

    // 응답 코드 부여 기관
    // 0: 계좌통합센터, 1: 금융기관
    // 예시 값: "0"
    // 타입: AN(1)
    private String rspType;

    // 금융 기관 구분
    // 예시 값: "1" - 현재 사실상 고정값
    // 타입: AN(1)
    private String inquiryBankType;

    // 조회 계좌 목록
    private List<AccountInfoDto> resList;

}
