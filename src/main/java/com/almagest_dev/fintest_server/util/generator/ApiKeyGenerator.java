package com.almagest_dev.fintest_server.util.generator;

import java.security.SecureRandom;
import java.util.Base64;

// API Key(인증키) 생성 유틸리티 클래스
public class ApiKeyGenerator {
    /**
     * 랜덤 256비트 API 키 생성
     * @return Base64 인코딩된 API 키 문자열
     */
    public static String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256-bit API 키
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
