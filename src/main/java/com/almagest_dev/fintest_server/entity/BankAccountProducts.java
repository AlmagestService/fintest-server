package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountProducts extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3, columnDefinition = "VARCHAR(3)")
    @Comment("기관 코드")
    private String orgCode;


    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    @Comment("상품 이름")
    private String productName;


    @Column(nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("상품 식별자")
    private String productCode;

    public BankAccountProducts(String orgCode, String productName, String description, BigDecimal interestRate) {
        this.orgCode = orgCode;
        this.productName = productName;
        this.description = description;
        this.interestRate = interestRate;
    }

    @PrePersist
    public void prePersist() {
        if (productCode == null) {
            productCode = UUID.randomUUID().toString();
        }
    }

    @Column(length = 255, columnDefinition = "VARCHAR(255)")
    @Comment("상품 설명")
    private String description;

    @Column(nullable = false, columnDefinition = "DECIMAL(5, 2)")
    @Comment("이자율")
    private BigDecimal interestRate;

}
