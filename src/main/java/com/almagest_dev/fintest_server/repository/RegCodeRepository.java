package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.RegCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegCodeRepository extends JpaRepository<RegCode, Long> {
    Optional<RegCode> findByRegCode(String regCode);
}
