package com.almagest_dev.fintest_server.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;


/**
 * API키 발급받을 개발자 계정
 * */
@Entity
@Getter
@Setter
@Table(name = "member")
@ToString
public class Member extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("사용자 서비스 식별자")
    private String serviceId;

    @Column(unique = true, nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("사용자 Almagest 식별자")
    private String almagestId;

    @Column(unique = true, nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("사용자 이메일")
    private String email;

    @Column(columnDefinition = "Date")
    @Comment("마지막 정보 업데이트")
    private LocalDateTime lastUpdate;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    @Comment("사용자 권한")
    private String role;

    @Column(nullable = false, length = 1, columnDefinition = "VARCHAR(1)")
    @Comment("사용 가능 여부")
    private String isAvailable;

    @OneToOne(mappedBy = "member")
    private ApiKey apiKey;

    @OneToOne(mappedBy = "member")
    private RegCode regCode;




}