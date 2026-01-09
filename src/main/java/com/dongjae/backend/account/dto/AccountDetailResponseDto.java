package com.dongjae.backend.account.dto;

import com.dongjae.backend.common.enums.AccountStatus;
import lombok.Getter;

@Getter
public class AccountDetailResponseDto {
    private String accountNumber;
    private Long balance;
    private AccountStatus status;
    private LimitResponseDto limits;

    public AccountDetailResponseDto(String accountNumber, Long balance, AccountStatus status, LimitResponseDto limits) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.limits = limits;
    }
}
