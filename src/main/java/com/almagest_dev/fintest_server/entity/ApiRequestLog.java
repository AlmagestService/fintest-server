package com.almagest_dev.fintest_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequestLog extends BaseTime{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("API 요청 식별자")
    private String apiTranId;

    @Column(nullable = false, length = 10, columnDefinition = "VARCHAR(10)")
    @Comment("API 요청 종류")
    private String reqType;

    @Column(nullable = false, length = 10, columnDefinition = "VARCHAR(10)")
    @Comment("API 요청 상태")
    private String reqStatus;


    @Column(nullable = false, length = 5, columnDefinition = "VARCHAR(5)")
    @Comment("HTTP 메서드")
    private String httpMethod;

    @Column(nullable = false, length = 3, columnDefinition = "VARCHAR(3)")
    @Comment("HTTP 상태 코드")
    private String httpStatusCode;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @Comment("요청 URL")
    private String requestUrl;

    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("클라이언트 IP주소")
    private String clientIp;

    @Column(nullable = false, length = 15, columnDefinition = "VARCHAR(15)")
    @Comment("API 처리 시간")
    private String responseTimeMs;

    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("요청 HOST")
    private String host;

}
