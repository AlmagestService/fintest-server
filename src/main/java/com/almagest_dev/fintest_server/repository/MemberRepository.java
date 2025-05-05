package com.almagest_dev.fintest_server.repository;


import com.almagest_dev.fintest_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAlmagestId(String almagestId);
}
