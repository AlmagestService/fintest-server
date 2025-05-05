package com.almagest_dev.fintest_server.service;

import com.almagest_dev.fintest_server.entity.Person;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 사용자(Person) 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    /**
     * 사용자 생성 - 개별 식별자 저장
     * @param serviceName 서비스 이름
     * @return 생성된 사용자 엔티티
     */
    public Person createPerson(String serviceName){
        if(serviceName == null || serviceName.equals("")){
            throw new BadRequestException("서비스 이름 누락");
        }
        return personRepository.save(new Person(serviceName));
    }
}

