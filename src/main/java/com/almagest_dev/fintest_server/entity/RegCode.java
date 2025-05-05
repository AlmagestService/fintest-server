package com.almagest_dev.fintest_server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Setter
@Getter
@Table(name = "reg_code")
public class RegCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reg_code", unique = true, nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("서비스 사용 등록 코드")
    private String regCode;

    @ManyToOne
    @JoinColumn(name = "ord_code")
    @Comment("연계 기관 코드")
    @JsonIgnore
    private OrgCode orgCode;

    @OneToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;
}
