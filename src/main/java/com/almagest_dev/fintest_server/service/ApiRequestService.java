package com.almagest_dev.fintest_server.service;

import com.almagest_dev.fintest_server.entity.ApiRequestLog;
import com.almagest_dev.fintest_server.entity.QApiRequestLog;
import com.almagest_dev.fintest_server.repository.ApiRequestLogRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// API 요청 로깅 및 상태 관리를 담당하는 서비스 클래스
@Service
@RequiredArgsConstructor
@Transactional
public class ApiRequestService {
    private final JPAQueryFactory query;
    private final ApiRequestLogRepository apiRequestLogRepository;

    /**
     * API 요청 객체 저장 - 요청타입과 상태 기본값 설정
     * @param request HTTP 요청 객체
     * @param reqType 요청 타입
     * @param apiTranId API 트랜잭션 ID
     * @return 저장된 API 요청 로그 엔티티
     */
    public ApiRequestLog newRequest(HttpServletRequest request, String reqType, String apiTranId){
        String httpMethod = request.getMethod();
        String requestUrl = request.getRequestURI();
        String clientIp = request.getRemoteAddr();
        String host = request.getServerName();

        ApiRequestLog apiRequestLog = new ApiRequestLog();
        apiRequestLog.setApiTranId(apiTranId);
        apiRequestLog.setReqType(reqType);
        apiRequestLog.setReqStatus("R");
        apiRequestLog.setRequestUrl(requestUrl);
        apiRequestLog.setHost(host);
        apiRequestLog.setHttpMethod(httpMethod);
        apiRequestLog.setClientIp(clientIp);
        apiRequestLog.setHttpStatusCode("REQ");
        apiRequestLog.setResponseTimeMs("0");

        return apiRequestLogRepository.save(apiRequestLog);
    }

    /**
     * API 요청 상태 업데이트
     * @param apiTranId API 트랜잭션 ID
     * @param apiReqStatus 요청 상태(S/F 등)
     * @param responseTime 응답 시간(ms)
     */
    public void updateStatus(String apiTranId, String apiReqStatus, long responseTime) {
        QApiRequestLog apiRequestLog = QApiRequestLog.apiRequestLog;
        long result = query.update(apiRequestLog)
                .set(apiRequestLog.reqStatus, apiReqStatus)
                .set(apiRequestLog.responseTimeMs, String.valueOf(responseTime))
                .where(apiRequestLog.apiTranId.eq(apiTranId))
                .execute();
    }


}
