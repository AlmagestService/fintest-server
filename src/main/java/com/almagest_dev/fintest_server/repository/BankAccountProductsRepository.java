package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.BankAccountProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountProductsRepository extends JpaRepository<BankAccountProducts, Long> {
}
