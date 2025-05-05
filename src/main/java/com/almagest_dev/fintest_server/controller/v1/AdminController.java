package com.almagest_dev.fintest_server.controller.v1;

import com.almagest_dev.fintest_server.dto.RegCodeDto;
import com.almagest_dev.fintest_server.service.AdminService;
import com.almagest_dev.fintest_server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 관련 API를 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/fintest/a3/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;
    private final AdminService adminService;

    /**
     * 기관 등록 API
     * @param regCodeDto 기관 등록 정보
     * @return 등록 결과 메시지
     */
    @PostMapping("/org")
    public ResponseEntity<String> registerOrg(@RequestBody RegCodeDto regCodeDto) {
        // 기관 등록 서비스 호출
        adminService.newOrg(regCodeDto);
        return ResponseEntity.ok("기관 등록 완료");
    }

    /**
     * 기관 등록 코드 생성 API
     * @param requestDto 코드 생성 요청 정보
     * @return 생성 결과 메시지
     */
    @PostMapping("/code")
    public ResponseEntity<?> createRegCode(@RequestBody RegCodeDto requestDto){
        // 등록 코드 생성 서비스 호출
        memberService.createRegisterCode(requestDto);
        return ResponseEntity.ok("코드 생성 성공");
    }

}
