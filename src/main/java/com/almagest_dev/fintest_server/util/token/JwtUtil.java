package com.almagest_dev.fintest_server.util.token;


import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

// JWT 토큰 검증 및 파싱을 위한 유틸리티 클래스
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.public-key}")
    private String SECRET_KEY;

    private final String JWT_ISSUER = "https://almagest.io";

    /**
     * publicKey 문자열로부터 RSA 공개키 객체 복원
     * @param publicKey 공개키 문자열
     * @return RSA 공개키 객체
     */
    private Key toPublicKey(String publicKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }catch (Exception e){
            log.error("공개 키 변환 중 오류 발생.", e);
            throw new BadRequestException("토큰 검증 오류.");
        }
    }

    /**
     * JWT 토큰에서 Almagest 식별자 추출
     * @param token JWT 토큰
     * @return subject(AlmagestId)
     */
    public String extractAlmagestId(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * JWT 토큰의 만료 시간 추출
     * @param token JWT 토큰
     * @return 만료 일시(Date)
     */
    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    /**
     * Access Token 유효성 검증
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            if(!claims.getIssuer().equals(JWT_ISSUER)){
                throw new JwtException("유효한 토큰이 아닙니다.");
            }
            return claims.getExpiration().after(new Date());  // 토큰이 만료되지 않음
        } catch (ExpiredJwtException e) {
            log.debug("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid token");
        }
        return false;  // 유효하지 않은 토큰
    }

    /**
     * JWT의 Claims 파싱 메서드
     * @param token JWT 토큰
     * @return Claims 객체
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(toPublicKey(SECRET_KEY))  // 서명 키 설정
                .build()
                .parseClaimsJws(token)  // JWT 파싱
                .getBody();
    }

}
