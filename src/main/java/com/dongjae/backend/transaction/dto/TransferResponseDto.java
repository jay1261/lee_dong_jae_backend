package com.dongjae.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferResponseDto {
    private Long transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Long amount;
    private Long fee;
    private Long balance;
}
