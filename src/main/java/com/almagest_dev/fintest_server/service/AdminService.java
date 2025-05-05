package com.almagest_dev.fintest_server.service;

import com.almagest_dev.fintest_server.dto.RegCodeDto;
import com.almagest_dev.fintest_server.entity.OrgCode;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.repository.OrgCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

// 관리자(기관) 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class AdminService {
    private final OrgCodeRepository orgCodeRepository;

    /**
     * 기관(OrgCode) 신규 등록
     * @param requestDto 기관 등록 요청 DTO
     */
    public void newOrg(RegCodeDto requestDto){
        if(requestDto.getOrgCode() == null || requestDto.getOrgCode().equals("")){
            throw new BadRequestException("기관 코드 누락");
        }

        if(requestDto.getOrgName() == null || requestDto.getOrgName().equals("")){
            throw new BadRequestException("기관 이름 누락");
        }

        OrgCode orgCode = new OrgCode();
        orgCode.setCode(requestDto.getOrgCode());
        orgCode.setName(requestDto.getOrgName());
        orgCode.setAccountLength(Long.valueOf(requestDto.getAccountLength()));
        orgCode.setOrgId(String.valueOf(UUID.randomUUID()));

        orgCodeRepository.save(orgCode);
    }
}
