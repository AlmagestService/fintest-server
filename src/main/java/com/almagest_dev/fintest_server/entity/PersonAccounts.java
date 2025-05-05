package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "person_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonAccounts extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_finance_id", length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("사용자 식별 코드") //전달받은 사용자 식별코드 주입받음
    private String userFinanceId;

    @Column(name = "fintech_use_num", length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("계좌 식별 코드")
    private String fintechUseNum;

    @Column(name = "account_num", unique = true, length = 20, nullable = false, columnDefinition = "VARCHAR(20)")
    @Comment("계좌번호") //랜덤한 16자리 생성
    private String accountNum;

    @Column(name = "account_holder", length = 20, nullable = false, columnDefinition = "VARCHAR(20)")
    @Comment("계좌 소유주") //랜덤한 한국이름 생성
    private String accountHolder;

    @Column(name = "bank_code", length = 3, nullable = false, columnDefinition = "VARCHAR(3)")
    @Comment("기관 코드") //상품에 들어있는 기관코드
    private String bankCode;

    @Column(name = "product_code",nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("상품 식별자")
    private String productCode;

    @Column(name = "verified", length = 1, nullable = false, columnDefinition = "VARCHAR(1)")
    @Comment("계좌 인증 여부") //대부분 Y, 소수 N
    private String verified;

    @Column(name = "current_balance", nullable = false, columnDefinition = "BIGINT")
    @Comment("현재 잔고") // 0~10000000 사이
    private Long currentBalance;

    @Column(name = "account_issue_date", nullable = false, columnDefinition = "DATETIME")
    @Comment("계좌 생성일")
    private LocalDateTime accountIssueDate;

    @Column(name = "last_transaction_date", columnDefinition = "DATETIME")
    @Comment("마지막 거래일")
    private LocalDateTime lastTransactionDate;

    @OneToMany(mappedBy = "personAccounts", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BankTransaction> transactions;


//    public String getCurrentBalance() {
//        return currentBalance != null ? currentBalance.toString() : null;
//    }

}
