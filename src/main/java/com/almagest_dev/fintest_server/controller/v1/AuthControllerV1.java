package com.almagest_dev.fintest_server.controller.v1;

import com.almagest_dev.fintest_server.dto.CommonResponseDto;
import com.almagest_dev.fintest_server.dto.member.MemberRequestDto;
import com.almagest_dev.fintest_server.entity.Member;
import com.almagest_dev.fintest_server.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 관련 API를 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/fintest/a2/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {
    private final MemberService memberService;

    /**
     * 사용등록 및 코드 입력 API
     * @param request 인증 요청 정보
     * @param requestDto 등록 코드 요청 정보
     * @return 등록 결과 응답 DTO
     */
    @PostMapping
    public ResponseEntity<?> registerCode(
            HttpServletRequest request,
            @RequestBody MemberRequestDto requestDto) {
        // 1. Almagest에 가입용 사용자 정보 요청
        Member member = memberService.requestRegisterMemberData(request);

        // 2. 입력된 코드 검증 후 등록
        memberService.EnrollRegisterCode(requestDto, member);

        CommonResponseDto<String> responseDto = new CommonResponseDto<>();
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("사용자 등록 성공");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
