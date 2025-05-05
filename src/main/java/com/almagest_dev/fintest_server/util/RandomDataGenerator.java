package com.almagest_dev.fintest_server.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

// 랜덤 데이터(이메일, 닉네임 등) 생성을 위한 유틸리티 클래스
@Component
public class RandomDataGenerator {

    /**
     * 랜덤 이메일 생성
     * @return 랜덤 이메일 문자열
     */
    public String generateRandomEmail() {
        String randomString = generateRandomString(8); // 8자리의 랜덤 문자열 생성
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // 날짜와 시간을 붙임
        return randomString + timestamp + "@example.com"; // 이메일 형식으로 반환
    }

    /**
     * 랜덤 닉네임 생성
     * @return 랜덤 닉네임 문자열
     */
    public String generateRandomNickName() {
        String randomString = generateRandomString(6); // 6자리의 랜덤 문자열 생성
        String timestamp = new SimpleDateFormat("HHmmss").format(new Date()); // 시간만 붙임
        return "User" + randomString + timestamp; // "User" + 랜덤 문자열 + 시간 형식으로 반환
    }

    /**
     * 랜덤 문자열 생성
     * @param length 생성할 문자열 길이
     * @return 랜덤 문자열
     */
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 사용할 문자들
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    /**
     * 마이데이터 연동요청시 임의 데이터 생성
     * @param userFinanceId 사용자 금융식별자
     */
    public void generateUserFinanceData(String userFinanceId){
        // ... 구현 필요 ...
    }
}
