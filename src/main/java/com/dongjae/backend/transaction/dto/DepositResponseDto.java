package com.dongjae.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositResponseDto {
    private Long transactionId;
    private String accountNumber;
    private Long amount;
    private Long balance;
}