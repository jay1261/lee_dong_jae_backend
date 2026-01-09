package com.dongjae.backend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponseDto {
    private String accountNumber;
    private LimitResponseDto limits;
}
