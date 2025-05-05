package com.almagest_dev.fintest_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlmagestInfoDto {
    private String memberId;
    private String account;
    private String isEnabled;
    private String name;
    private String email;
    private LocalDateTime lastUpdate;
}
