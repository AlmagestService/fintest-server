package com.almagest_dev.fintest_server.util;

// 거래 코드에 따라 입금/출금 타입을 반환하는 유틸리티 클래스
public class TranTypePicker {
    /**
     * 거래 코드로 거래 타입(입금/출금) 반환
     * @param tranCode 거래 코드
     * @return "입금", "출금" 또는 null
     */
    public static String getTranType(String tranCode){
        switch (tranCode) {
            case "11", "44" -> {
                return "입금";
            }
            case "22", "33" -> {
                return "출금";
            }
            default -> {
                return null;
            }
        }
    }
}
