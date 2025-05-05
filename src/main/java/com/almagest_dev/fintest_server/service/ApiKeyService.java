package com.almagest_dev.fintest_server.service;


import com.almagest_dev.fintest_server.entity.*;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.exception.base_exceptions.DataAccessFailException;
import com.almagest_dev.fintest_server.exception.base_exceptions.ValidationException;
import com.almagest_dev.fintest_server.repository.ApiKeyRepository;
import com.almagest_dev.fintest_server.repository.MemberRepository;
import com.almagest_dev.fintest_server.util.generator.ApiKeyGenerator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// API 키 발급, 검증, 관리 등 인증키 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory query;

    // 일일 API 호출량 제한
    private static final int API_CALL_COUNT = 1000;

    /**
     * API키 생성 및 저장
     * @param userId 사용자 ID
     * @return 생성된 API키 엔티티
     */
    public ApiKey generateAndSaveApiKey(Long userId) {

        String apiKey;
        int attempts = 0; // 최대 시도 횟수 제한을 위해
        final int MAX_ATTEMPTS = 10;

        do {
            if (attempts >= MAX_ATTEMPTS) {
                throw new DataAccessFailException("Error! try again.");
            }
            apiKey = ApiKeyGenerator.generateApiKey(); // 새 키 생성
            attempts++;
        } while (apiKeyRepository.findByApiKey(apiKey).isPresent());

            Member member = memberRepository.findById(userId).orElseThrow(() -> new BadRequestException("Member not found"));

            ApiKey newApiKey = new ApiKey();
            newApiKey.setMember(member);
            newApiKey.setApiKey(apiKey);
            newApiKey.setApiCallCount(API_CALL_COUNT);
            newApiKey.setIsAvailable("T");
            ApiKey saved = apiKeyRepository.save(newApiKey);

            member.setApiKey(saved);
            memberRepository.save(member);
        return saved;
    }

    /**
     * 사용자의 API키 조회
     * @param userId 사용자 ID
     * @return API키 Optional
     */
    public Optional<ApiKey> getApiKeyByUserId(Long userId) {
        return apiKeyRepository.findByMemberId(userId);
    }

    /**
     * API키 연계 기관(OrgCode) 조회
     * @param regCode 등록 코드
     * @return 기관 정보 엔티티
     */
    public OrgCode getOrgCode(String regCode){
        QRegCode qRegCode = QRegCode.regCode1;

        RegCode savedRegCode = query.selectFrom(qRegCode)
                .where(qRegCode.regCode.eq(regCode))
                .fetchOne();

        if(savedRegCode == null){
            throw new BadRequestException("기관 코드 조회 실패");
        }

        return savedRegCode.getOrgCode();
    }

    /**
     * API키 재발급
     * @param memberId 사용자 ID
     * @return 재발급된 API키 엔티티
     */
    public ApiKey regenerateApiKey(Long memberId) {
        Optional<ApiKey> existingApiKey = apiKeyRepository.findByMemberId(memberId);
        existingApiKey.ifPresent(apiKeyRepository::delete);
        return generateAndSaveApiKey(memberId);
    }

    /**
     * API키 호출 횟수 차감
     * @param apiKey API키 값
     */
    public void decreaseCallCount(String apiKey){
        Optional<ApiKey> key = apiKeyRepository.findByApiKey(apiKey);
        ApiKey tmp = key.orElseThrow(() -> new ValidationException("API-KEY 검증 실패"));
        tmp.decreaseCallCount();
        apiKeyRepository.save(tmp);
    }

    /**
     * 일일 호출횟수 초기화 (스케줄러)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetCallCounts(){
        QApiKey qApiKey = QApiKey.apiKey1;
        log.info("Starting daily API usage reset...");
        try {
            long updatedCount = query.update(qApiKey)
                    .set(qApiKey.apiCallCount, API_CALL_COUNT)
                    .execute();
            log.info("Daily API usage reset complete. Updated {} records.", updatedCount);
        }catch (Exception e){
            log.error("Failed to reset API usage counts", e);
            throw new DataAccessFailException("API usage reset failed: " + e.getMessage());
        }
    }
}
