package com.almagest_dev.fintest_server.service;


import com.almagest_dev.fintest_server.dto.CommonResponseDto;
import com.almagest_dev.fintest_server.dto.RegCodeDto;
import com.almagest_dev.fintest_server.dto.member.AlmagestInfoDto;
import com.almagest_dev.fintest_server.dto.member.MemberRequestDto;
import com.almagest_dev.fintest_server.entity.Member;
import com.almagest_dev.fintest_server.entity.OrgCode;
import com.almagest_dev.fintest_server.entity.RegCode;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.repository.MemberRepository;
import com.almagest_dev.fintest_server.repository.OrgCodeRepository;
import com.almagest_dev.fintest_server.repository.RegCodeRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.almagest_dev.fintest_server.util.constants.ExternalURL.ALMAGEST_INFO_UPDATE_URL;
import static com.almagest_dev.fintest_server.util.constants.ExternalURL.ALMAGEST_REGISTER_DATA_URL;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final OrgCodeRepository orgCodeRepository;
    private final RegCodeRepository regCodeRepository;

    /**
     * 쿠키에 담긴 토큰으로 사용자 정보를 요청
     */
    public Member requestRegisterMemberData(HttpServletRequest request){

        // 쿠키에서 토큰 추출
        StringBuilder cookieHeader = new StringBuilder();

        // 쿠키에서 access_token과 refresh_token만 추출하여 헤더에 추가
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName()) || "refresh_token".equals(cookie.getName())) {
                    cookieHeader.append(cookie.getName())
                            .append("=")
                            .append(cookie.getValue())
                            .append("; ");
                }
            }
        }
        if (cookieHeader.length() == 0) {
            throw new IllegalStateException("인증정보가 누락되었습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookieHeader.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CommonResponseDto<AlmagestInfoDto>> response = restTemplate.exchange(
                ALMAGEST_REGISTER_DATA_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );


        AlmagestInfoDto responseData = response.getBody().getData();


        if (responseData != null) {

            if(responseData.getIsEnabled().equals("F")){
                throw new BadRequestException("승인되지 않은 사용자 입니다");
            }
            Optional<Member> existingMember = memberRepository.findByAlmagestId(responseData.getMemberId());
            if (existingMember.isPresent()) {
                return existingMember.get();
            }

            Member member = new Member();

            member.setAlmagestId(responseData.getMemberId());
            member.setEmail(responseData.getEmail());
            member.setRole("ROLE_USER"); // 기본 사용자 권한
            member.setIsAvailable("F");
            member.setServiceId(String.valueOf(UUID.randomUUID()));
            return memberRepository.save(member);
        } else {
            throw new BadRequestException("가입정보 수신 실패");
        }
    }

    /**
     * 쿠키에 담긴 토큰으로 사용자 최신 정보를 요청
     */
    public void requestLatestMemberData(HttpServletRequest request){


        // 쿠키에서 토큰 추출
        StringBuilder cookieHeader = new StringBuilder();


        // 쿠키에서 access_token과 refresh_token만 추출하여 헤더에 추가
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName()) || "refresh_token".equals(cookie.getName())) {
                    cookieHeader.append(cookie.getName())
                            .append("=")
                            .append(cookie.getValue())
                            .append("; ");
                }
            }
        }
        if (cookieHeader.length() == 0) {
            throw new IllegalStateException("인증정보가 누락되었습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookieHeader.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<AlmagestInfoDto> response = restTemplate.exchange(
                ALMAGEST_INFO_UPDATE_URL,
                HttpMethod.POST,
                requestEntity,
                AlmagestInfoDto.class
        );

        AlmagestInfoDto responseData = response.getBody();

        if (responseData != null) {

            if(responseData.getIsEnabled().equals("F")){
                throw new BadRequestException("승인되지 않은 사용자 입니다");
            }
            Optional<Member> existingMember = memberRepository.findByAlmagestId(responseData.getMemberId());

            if(existingMember.isEmpty()){
                throw new BadRequestException("사용자 정보가 없습니다");
            }

            Member member = existingMember.get();

            member.setEmail(responseData.getEmail());
            member.setLastUpdate(responseData.getLastUpdate());
            memberRepository.save(member);
        } else {
            throw new BadRequestException("가입정보 수신 실패");
        }
    }

    /**
     * API 등록 코드 생성
     */
    public void createRegisterCode(RegCodeDto requestDto){
        String orgCode = requestDto.getOrgCode();
        int size = Integer.parseInt(requestDto.getSize());
        if(orgCode == null || orgCode.equals("") || size == 0){
            throw new BadRequestException("코드 생성 정보 누락");
        }

        Optional<OrgCode> savedOrg = orgCodeRepository.findByCode(orgCode);
        if(savedOrg.isEmpty()){
            throw new BadRequestException("기관정보 조회 실패");
        }

        OrgCode org = savedOrg.get();

        // 전달된 수 만큼 생성
        try {
            List<RegCode> regCodeList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                RegCode tmp = new RegCode();
                String registerCode = UUID.randomUUID().toString();
                tmp.setOrgCode(org);
                tmp.setRegCode(registerCode);
                regCodeList.add(tmp);
            }

            regCodeRepository.saveAll(regCodeList);
        }catch (Exception e){
            throw new BadRequestException("코드 생성 실패");
        }
    }

    /**
     * API 등록 코드 검증
     * */
    public void EnrollRegisterCode(MemberRequestDto requestDto, Member member){

        if (requestDto.getOrgId() == null || requestDto.getRegCode() == null) {
            throw new BadRequestException("코드 누락");
        }

        Optional<RegCode> tmp = regCodeRepository.findByRegCode(requestDto.getRegCode());

        if(tmp.isEmpty()){
            throw new BadRequestException("잘못된 코드입니다");
        }

        RegCode savedRegCode = tmp.get();

        if(savedRegCode.getMember() != null){
            throw new BadRequestException("이미 사용중인 코드입니다");
        }

        if(!String.valueOf(savedRegCode.getOrgCode().getOrgId()).equals(requestDto.getOrgId())){
            throw new BadRequestException("기관 코드 불일치");
        }

        if(!savedRegCode.getRegCode().equals(requestDto.getRegCode())){
            throw new BadRequestException("등록 코드 불일치");
        }

        savedRegCode.setMember(member);

        regCodeRepository.save(savedRegCode);

        member.setIsAvailable("T");

        memberRepository.save(member);
    }

    /**
     * Almagest 식별자로 사용자 조회
     */
    public Member getRegisteredMemberInfo(Member member) {

        Optional<Member> byAlmagestId = memberRepository.findByAlmagestId(member.getAlmagestId());

        if(byAlmagestId.isEmpty()){
            throw new BadRequestException("사용자 조회 실패. 등록 후 사용하세요.");
        }

        return byAlmagestId.get();
    }
}
