package com.almagest_dev.fintest_server.repository;

import com.almagest_dev.fintest_server.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
}
