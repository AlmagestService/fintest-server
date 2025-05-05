package com.almagest_dev.fintest_server.util;

import org.springframework.stereotype.Component;

// 계좌번호 등 개인정보 마스킹 처리를 위한 유틸리티 클래스
@Component
public class MaskUtil {

    /**
     * 계좌번호 마스킹 처리
     * @param accountNumber 계좌번호 문자열
     * @return 마스킹된 계좌번호
     */
    public static String maskAccountNumber(String accountNumber) {
        return accountNumber.replaceAll(".(?=.{5})", "*");
    }

}
