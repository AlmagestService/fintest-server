package com.almagest_dev.fintest_server.controller.v1;

import com.almagest_dev.fintest_server.dto.CommonResponseDto;
import com.almagest_dev.fintest_server.dto.member.MemberRequestDto;
import com.almagest_dev.fintest_server.dto.member.MemberResponseDto;
import com.almagest_dev.fintest_server.entity.CustomUserDetails;
import com.almagest_dev.fintest_server.entity.Member;
import com.almagest_dev.fintest_server.entity.OrgCode;
import com.almagest_dev.fintest_server.service.ApiKeyService;
import com.almagest_dev.fintest_server.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

// 회원 관련 API를 처리하는 컨트롤러 클래스
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fintest/a2/v1")
public class MemberControllerV1 {

    private final MemberService memberService;
    private final ApiKeyService apiKeyService;

    /**
     * 회원 정보 조회 API
     * @param userDetails 인증된 사용자 정보
     * @return 회원 정보 응답 DTO
     */
    @GetMapping("/member")
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        Member member = userDetails.getMember();
        OrgCode orgCode = apiKeyService.getOrgCode(member.getRegCode().getRegCode());
        CommonResponseDto<MemberResponseDto> responseDto = new CommonResponseDto<>();
        MemberResponseDto memberResponseDto = toMemberResponseDto(member, orgCode);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("사용자 조회 성공");
        responseDto.setData(memberResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 회원 정보 최신화 API
     * @param request HTTP 요청 객체
     * @param userDetails 인증된 사용자 정보
     * @return 처리 결과 메시지
     */
    @PutMapping("/member")
    public ResponseEntity<?> updateMember(HttpServletRequest request,
                                          @AuthenticationPrincipal CustomUserDetails userDetails){
        // Almagest에 사용자 최신 정보 요청
        memberService.requestLatestMemberData(request);
        CommonResponseDto<String> responseDto = new CommonResponseDto<>();
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("사용자 정보 최신화 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 인증 갱신 API
     * @param userDetails 인증된 사용자 정보
     * @return 회원 정보 응답 DTO
     */
    @GetMapping("/renew")
    public ResponseEntity<?> renewAccess(@AuthenticationPrincipal CustomUserDetails userDetails){
        Member member = userDetails.getMember();
        OrgCode orgCode = apiKeyService.getOrgCode(member.getRegCode().getRegCode());
        CommonResponseDto<MemberResponseDto> responseDto = new CommonResponseDto<>();
        MemberResponseDto memberResponseDto = toMemberResponseDto(member, orgCode);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("인증 갱신 성공");
        responseDto.setData(memberResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 사용자 정보 Dto 변환
     * @param member 회원 엔티티
     * @param orgCode 기관 정보 엔티티
     * @return 회원 응답 DTO
     */
    private static MemberResponseDto toMemberResponseDto(Member member, OrgCode orgCode) {
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        String apiKey = "";
        int callCount = 0;
        if(member.getApiKey() != null) {
            apiKey = member.getApiKey().getApiKey();
            callCount = member.getApiKey().getApiCallCount();
        }
        memberResponseDto.setServiceId(member.getServiceId());
        memberResponseDto.setAlmagestId(member.getAlmagestId());
        memberResponseDto.setIsAvailable(member.getIsAvailable());
        memberResponseDto.setApiKey(apiKey);
        memberResponseDto.setApiCallCount(callCount);
        memberResponseDto.setOrgName(orgCode.getName());
        return memberResponseDto;
    }
}
