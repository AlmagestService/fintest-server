package com.almagest_dev.fintest_server.dto.recipient;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountHolderInquiryRequestDto {
    private String orgCode;
    private String accountNum;
}
