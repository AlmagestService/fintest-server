package com.almagest_dev.fintest_server.util.token;


/**
 * Access/Refresh 토큰 값을 저장하는 클래스
 */
public class Tokens {
    private final String accessToken;
    private final String refreshToken;

    /**
     * 토큰 객체 생성자
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레시 토큰
     */
    public Tokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * 액세스 토큰 반환
     * @return 액세스 토큰
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * 리프레시 토큰 반환
     * @return 리프레시 토큰
     */
    public String getRefreshToken() {
        return this.refreshToken;
    }
}
