package com.almagest_dev.fintest_server.controller.v1;

import com.almagest_dev.fintest_server.dto.ApiKeyDto;
import com.almagest_dev.fintest_server.dto.CommonResponseDto;
import com.almagest_dev.fintest_server.entity.ApiKey;
import com.almagest_dev.fintest_server.entity.CustomUserDetails;
import com.almagest_dev.fintest_server.entity.Member;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// API 키 관련 기능을 제공하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/fintest/a2/v1/core/keys")
@RequiredArgsConstructor
public class ApiKeyControllerV1 {
    private final ApiKeyService apiKeyService;

    /**
     * API 키 생성 API
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 API 키 정보
     */
    @PostMapping
    public ResponseEntity<?> generateApiKey(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        CommonResponseDto<ApiKeyDto> responseDto = new CommonResponseDto<>();
        ApiKey apiKey = apiKeyService.generateAndSaveApiKey(member.getId());
        ApiKeyDto apiKeyDto = new ApiKeyDto();
        apiKeyDto.setApiKey(apiKey.getApiKey());
        apiKeyDto.setApiCallCount(apiKey.getApiCallCount());
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("API-KEY 생성 성공");
        responseDto.setData(apiKeyDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * API 키 재생성 API
     * @param userDetails 인증된 사용자 정보
     * @return 재생성된 API 키 정보
     */
    @PutMapping
    public ResponseEntity<?> regenerateApiKey(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        CommonResponseDto<ApiKeyDto> responseDto = new CommonResponseDto<>();
        ApiKey newApiKey = apiKeyService.regenerateApiKey(member.getId());
        ApiKeyDto apiKeyDto = new ApiKeyDto();
        apiKeyDto.setApiKey(newApiKey.getApiKey());
        apiKeyDto.setApiCallCount(newApiKey.getApiCallCount());
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("API-KEY 재생성 성공");
        responseDto.setData(apiKeyDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 사용자별 API 키 조회 API
     * @param userDetails 인증된 사용자 정보
     * @return 조회된 API 키 정보
     */
    @GetMapping
    public ResponseEntity<?> getApiKeyByUserId(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        CommonResponseDto<ApiKeyDto> responseDto = new CommonResponseDto<>();
        ApiKey key = apiKeyService.getApiKeyByUserId(member.getId()).orElseThrow(() -> new BadRequestException("APIKEY not found"));
        ApiKeyDto apiKeyDto = new ApiKeyDto();
        apiKeyDto.setApiKey(key.getApiKey());
        apiKeyDto.setApiCallCount(key.getApiCallCount());
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("API-KEY 조회 성공");
        responseDto.setData(apiKeyDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
