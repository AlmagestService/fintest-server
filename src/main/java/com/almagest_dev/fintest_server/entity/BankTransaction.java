package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_account_id")
    private PersonAccounts personAccounts;

    @Column(nullable = false, columnDefinition = "BIGINT")
    @Comment("거래 금액")
    private Long amount;

    @Column(name = "tran_type_code", length = 2, nullable = false, columnDefinition = "INT")
    @Comment("요청 타입") //11(입금), 22(출금), 33(송금-출금), 44(송금-입금)
    private String tranTypeCode;

    @Column(name = "api_tran_id", length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("API 요청 식별자")
    private String apiTranId;

    @Column(nullable = false, columnDefinition = "DATETIME")
    @Comment("요청 일자")
    private LocalDateTime transactionDate;

    @Column(length = 255, columnDefinition = "VARCHAR(255)")
    @Comment("설명")
    private String description;

    @Column(name = "fintech_use_num", length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("출금 계좌 식별 코드")
    private String fintechUseNum;

    @Column(columnDefinition = "INT")
    @Comment("출금 기관 코드")
    private Integer withdrawalOrgCode;

    @Column(length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("출금 계좌번호")
    private String withdrawalAccountNumber;

    @Column(columnDefinition = "INT")
    @Comment("입금 기관 코드")
    private Integer depositOrgCode;

    @Column(length = 20, columnDefinition = "VARCHAR(255)")
    @Comment("입금 계좌번호")
    private String depositAccountNumber;

    @Column(nullable = false, columnDefinition = "BIGINT")
    @Comment("거래 전 잔고")
    private Long beforeBalance;

    @Column(nullable = false, columnDefinition = "BIGINT")
    @Comment("거래 후 잔고")
    private Long afterBalance;

    @Column(length = 1, columnDefinition = "VARCHAR(1)")
    @Comment("상태(R, S, F)")
    private String status;

    // 예시 값: "A0000"
    @Column(length = 5, columnDefinition = "VARCHAR(5)")
    @Comment("응답코드")
    private String rspCode;
}

