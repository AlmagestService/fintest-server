package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
}
