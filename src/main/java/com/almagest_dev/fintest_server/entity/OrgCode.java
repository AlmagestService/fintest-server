package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.UUID;


@Entity
@Setter
@Getter
@Table(name = "org_code")
public class OrgCode extends BaseTime{

    @Id
    @Column(name = "code", unique = true, length = 3, nullable = false, columnDefinition = "VARCHAR(3)")
    @Comment("기관 코드")
    private String code;

    @Column(name = "name", unique = true, length = 100, nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("기관 이름")
    private String name;

    @Column(name = "account_length", length = 2, columnDefinition = "INT")
    @Comment("계좌번호 길이")
    private Long accountLength;

    @Column(name = "org_id", length = 40, unique = true, nullable = false, columnDefinition = "VARCHAR(40) COMMENT '기관 식별 코드'")
    private String orgId;
}