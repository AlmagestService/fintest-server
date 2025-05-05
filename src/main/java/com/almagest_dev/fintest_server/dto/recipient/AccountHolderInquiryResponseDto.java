package com.almagest_dev.fintest_server.dto.recipient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountHolderInquiryResponseDto {
    private String orgCode;
    private String bankName;
    private String accountNum;
    private String accountHolderName;
    private String accountType;
}
