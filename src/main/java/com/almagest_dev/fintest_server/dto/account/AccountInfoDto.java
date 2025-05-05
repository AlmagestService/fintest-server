package com.almagest_dev.fintest_server.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDto {
    // 계좌 상세 정보를 담는 DTO 클래스

    // 개설 기관 코드
    // 예시 값: "097"
    // 타입: AN(3)
    private String bankCodeStd;

    // 사용자 계좌 식별자
    private String fintechUseNum;

    //계좌주명
    private String accountHolder;

    // 유형 구분
    // A: 활동성 계좌, I: 비활동성 계좌
    // 예시 값: "A"
    // 타입: A(1)
    private String activityType;

    // 계좌 종류
    // 예시 값: "1"
    // 타입: AN(1)
    private String accountType;

    // 계좌 번호
    // 예시 값: "0001234567890123"
    // 타입: AN(20)
    private String accountNum;

    // 회차 번호
    // 예시 값: "01"
    // 타입: AN(2)
    private String accountSeq;

    // 계좌 개설일
    // 예시 값: "20200918"
    // 타입: N(8)
    private String accountIssueDate;

    // 만기일
    // 예시 값: "20210917"
    // 타입: N(8)
    private String maturityDate;

    // 최종 거래일
    // 예시 값: "20200924"
    // 타입: N(8)
    private String lastTranDate;

    // 상품명 (계좌명)
    // 예시 값: "내맘대로통장"
    // 타입: AH(100)
    private String productName;

    // 부기명
    // 예시 값: "부기명"
    // 타입: AH(10)
    private String productSubName;

    // 휴면 계좌 여부
    // 예시 값: "N"
    // 타입: A(1)
    private String dormancyYn;

    // 예시 값: "1000000"
    // 타입: SN(15)
    private String balanceAmt;

}
