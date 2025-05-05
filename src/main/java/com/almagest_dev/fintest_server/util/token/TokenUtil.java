package com.almagest_dev.fintest_server.util.token;

import com.almagest_dev.fintest_server.exception.base_exceptions.DataAccessFailException;
import com.almagest_dev.fintest_server.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.almagest_dev.fintest_server.util.constants.ExternalURL.ALMAGEST_AUTH_SERVER_RENEW_URL;
import static com.almagest_dev.fintest_server.util.constants.ExternalURL.ALMAGEST_LOGIN_PAGE_URL;

// JWT/쿠키 기반 인증 및 토큰 추출, 인증 처리 유틸리티 클래스
@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 요청에서 토큰 추출 (쿠키 기반)
     * @param request HTTP 요청 객체
     * @return Tokens 객체 (access/refresh)
     */
    public Tokens extractTokens(HttpServletRequest request) {
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        return new Tokens(accessToken, refreshToken);
    }

    /**
     * 응답에서 토큰 추출 (Set-Cookie 기반)
     * @param tokenResponse 토큰 응답
     * @return Tokens 객체 (access/refresh)
     */
    public Tokens extractCookiesFromResponse(ResponseEntity<String> tokenResponse) {
        // HttpHeaders에서 Set-Cookie 헤더 가져오기
        HttpHeaders headers = tokenResponse.getHeaders();
        List<String> setCookieHeaders = headers.get(HttpHeaders.SET_COOKIE);

        if (setCookieHeaders != null) {
            String accessToken = null;
            String refreshToken = null;

            // Set-Cookie 헤더에서 access_token과 refresh_token 추출
            for (String cookie : setCookieHeaders) {
                if (cookie.startsWith("access_token=")) {
                    accessToken = cookie.split(";")[0].split("=")[1]; // access_token 값 추출
                } else if (cookie.startsWith("refresh_token=")) {
                    refreshToken = cookie.split(";")[0].split("=")[1]; // refresh_token 값 추출
                }
            }

            return new Tokens(accessToken, refreshToken);
        } else {
            System.out.println("No cookies found in the response.");
        }
        return null;
    }

    /**
     * 인증토큰 갱신 요청 및 인증 처리
     * @param refreshToken 리프레시 토큰
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws IOException
     * @throws ServletException
     */
    public void handleRefreshToken(String refreshToken, HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "refresh_token=" + refreshToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<String> tokenResponse = restTemplate.exchange(
                ALMAGEST_AUTH_SERVER_RENEW_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 토큰 추출
        Tokens tokens = extractCookiesFromResponse(tokenResponse);

        if(tokens == null){
            throw new DataAccessFailException("인증 실패");
        }

        String newAccessToken = tokens.getAccessToken();



        if (tokenResponse.getStatusCode().is2xxSuccessful()) {
            if (newAccessToken != null) {

                authenticateUser(newAccessToken, request);

                filterChain.doFilter(request, response);

            } else {
                redirectToLogin(response, request);
            }
        } else {
            redirectToLogin(response, request);
        }
    }

    /**
     * 사용자 인증 처리
     * @param accessToken 액세스 토큰
     * @param request HTTP 요청 객체
     */
    public void authenticateUser(String accessToken, HttpServletRequest request) {
        String almagestId = jwtUtil.extractAlmagestId(accessToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(almagestId);


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    /**
     * 로그인 페이지로 Redirect
     * @param response HTTP 응답 객체
     * @param request HTTP 요청 객체
     * @throws IOException
     */
    public void redirectToLogin(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.sendRedirect(ALMAGEST_LOGIN_PAGE_URL + "?redirect_uri=" + URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8));
    }

}
