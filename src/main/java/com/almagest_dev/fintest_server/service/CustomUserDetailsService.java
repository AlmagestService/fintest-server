package com.almagest_dev.fintest_server.service;

import com.almagest_dev.fintest_server.entity.CustomUserDetails;
import com.almagest_dev.fintest_server.entity.Member;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Spring Security에서 사용자 인증 정보를 제공하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 사용자 ID(AlmagestId)로 사용자 정보를 조회하여 UserDetails 반환
     * @param almagestId 사용자 식별자
     * @return UserDetails (Spring Security 인증 객체)
     * @throws UsernameNotFoundException 사용자 미존재 시 예외
     */
    @Override
    public UserDetails loadUserByUsername(String almagestId) throws UsernameNotFoundException {
        Optional<Member> savedMember = memberRepository.findByAlmagestId(almagestId);
        if(savedMember.isEmpty()){
            throw new BadRequestException("사용자를 찾을 수 없습니다.");
        }
        Member member = savedMember.get();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new CustomUserDetails(member, authorities);
    }
}
