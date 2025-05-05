package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.ApiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, Long> {
}
