package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.PersonAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonAccountsRepository extends JpaRepository<PersonAccounts, Long> {
    List<PersonAccounts> findByUserFinanceId(String userFinanceId);
}
