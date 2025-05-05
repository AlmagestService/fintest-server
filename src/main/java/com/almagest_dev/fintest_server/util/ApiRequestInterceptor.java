package com.almagest_dev.fintest_server.util;

import com.almagest_dev.fintest_server.entity.ApiRequestLog;
import com.almagest_dev.fintest_server.service.ApiRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 인터셉터 - API 로깅을 위한 다양한 정보를 저장 및 상태값 수정
 * */
@Component
@RequiredArgsConstructor
public class ApiRequestInterceptor implements HandlerInterceptor {

    private final ApiRequestService apiRequestService;

    /**
     * 컨트롤러 진입 전(PreHandle) - 요청 정보 기록 및 로그 생성
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 핸들러 객체
     * @return true(요청 계속 진행)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청 시작 시간 기록
        request.setAttribute("startTime", System.currentTimeMillis());

        // API 요청 ID 생성
        String apiTranId = UUID.randomUUID().toString();
        request.setAttribute("apiTranId", apiTranId);

        // API 요청 로그 생성
        String reqType = extractRequestType(request.getRequestURI());
        ApiRequestLog apiRequestLog = apiRequestService.newRequest(request, reqType, apiTranId);

        request.setAttribute("apiRequestLog", apiRequestLog);

        return true; // 요청 처리 계속 진행
    }

    /**
     * 컨트롤러 처리 후(AfterCompletion) - 응답 시간 및 상태 기록
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 핸들러 객체
     * @param ex 예외
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        String apiTranId = (String) request.getAttribute("apiTranId");

        if (startTime != null && apiTranId != null) {
            // 처리 시간 계산
            long responseTime = System.currentTimeMillis() - startTime;

            // 상태 코드 확인
            String status = (response.getStatus() == HttpServletResponse.SC_OK) ? "S" : "F";
            apiRequestService.updateStatus(apiTranId, status, responseTime);
        }
    }

    /**
     * 요청 URI로부터 요청 타입 추출
     * @param requestUri 요청 URI
     * @return 요청 타입 코드
     */
    private String extractRequestType(String requestUri) {
        if (requestUri.contains("/transfer")) {
            return "TR";
        } else if (requestUri.contains("/recipient")) {
            return "GR";
        } else if (requestUri.contains("/tranlist")) {
            return "GT";
        } else if (requestUri.contains("/account")) {
            return "SA";
        } else if (requestUri.contains("/accounts")) {
            return "IA";
        }
        return "UNKNOWN";
    }
}

