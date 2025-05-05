package com.almagest_dev.fintest_server.filter;

import com.almagest_dev.fintest_server.util.token.JwtUtil;
import com.almagest_dev.fintest_server.util.token.TokenUtil;
import com.almagest_dev.fintest_server.util.token.Tokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// JWT 인증 및 토큰 검증을 담당하는 Spring Security 필터 클래스
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;

    // 허용된 경로 목록 정의 
    /* 실제 API 엔드포인트 전체 명시 */
    private final List<String> allowedPaths = List.of(
            "/api/fintest/a1/v1/openbank/accounts",
            "/api/fintest/a1/v1/openbank/account",
            "/api/fintest/a1/v1/openbank/tranlist",
            "/api/fintest/a1/v1/openbank/recipient",
            "/api/fintest/a1/v1/openbank/transfer",
            "/api/fintest/a2/v1/core/keys",
            "/api/fintest/a2/v1/member",
            "/api/fintest/a2/v1/renew",
            "/api/fintest/a2/v1/auth",
            "/api/fintest/a3/v1/admin/org",
            "/api/fintest/a3/v1/admin/code"
    );

    /**
     * JWT 인증 및 토큰 검증 필터 동작
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();


        // 요청 경로가 허용된 목록에 없는 경우 400 반환
        if (allowedPaths.stream().noneMatch(requestPath::startsWith)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("잘못된 주소입니다.");
            return;
        }

        // 특정 경로(/api 등)는 인증 없이도 접근 가능하도록 예외 처리
        if (request.getServletPath().contains("/openbank")) {
            filterChain.doFilter(request, response);
            return;
        }

        // OPTIONS 요청은 예외 처리
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            System.out.println(request.getMethod());
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        // 토큰 추출
        Tokens tokens = tokenUtil.extractTokens(request);

        String accessToken = tokens.getAccessToken();
        String refreshToken = tokens.getRefreshToken();

        try {


            // 토큰이 모두 만료되거나 없는 경우
            if (accessToken == null && refreshToken == null) {
                tokenUtil.redirectToLogin(response, request);
            }

            if (accessToken != null && jwtUtil.validateAccessToken(accessToken)) {
                // 유효한 Access Token 인증 처리
                tokenUtil.authenticateUser(accessToken, request);
                filterChain.doFilter(request, response);
            }

            // Access 토큰이 없는경우
            if (refreshToken != null) {
                // Refresh Token을 사용하여 Access Token 갱신
                tokenUtil.handleRefreshToken(refreshToken, request, response, filterChain);
            }
        } catch (Exception e) {
            tokenUtil.redirectToLogin(response, request);
        }
    }
}