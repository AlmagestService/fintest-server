package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.UUID;

/**
 * 각 서비스에서 사용될 사용자 식별용
 * */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_finance_id", length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("생성된 사용자의 식별자")
    private String userFinanceId;

    @Column(name = "service_name", length = 50, columnDefinition = "VARCHAR(50)")
    @Comment("사용되는 서비스 이름")
    private String serviceName;

    public Person(String serviceName) {
        this.serviceName = serviceName;
    }
}
