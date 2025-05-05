package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
public class TransactionType {
    @Id
    @Column(name = "tran_type_code", length = 10, nullable = false, columnDefinition = "VARCHAR(10)")
    @Comment("트랜잭션 타입 코드")
    private Long tranTypeCode;

    @Column(name = "tran_type_desc", length = 20, nullable = false, columnDefinition = "VARCHAR(20)")
    @Comment("거래 종류")
    private String tranTypeDesc;
}
