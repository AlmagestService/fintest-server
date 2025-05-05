package com.almagest_dev.fintest_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long memberId;
    private String serviceId;
    private String almagestId;

    private LocalDateTime lastUpdate;
    private String isAvailable;

    private String email;
    private String apiKey;
    private Integer apiCallCount;

    private String orgName;

}
