package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.OrgCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrgCodeRepository extends JpaRepository<OrgCode, String> {
    Optional<OrgCode> findByCode(String code);
}
