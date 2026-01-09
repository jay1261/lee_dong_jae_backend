package com.dongjae.backend.account.dto;

import com.dongjae.backend.account.entity.Account;
import lombok.Getter;

@Getter
public class AccountSummaryResponseDto {
    private final String accountNumber;
    private final String status;

    public AccountSummaryResponseDto(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.status = account.getStatus().name();
    }
}