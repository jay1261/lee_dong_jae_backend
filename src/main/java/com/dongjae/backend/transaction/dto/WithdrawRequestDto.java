package com.dongjae.backend.transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WithdrawRequestDto {
    @NotNull(message = "WITHDRAW_AMOUNT_REQUIRED")
    @Min(value = 1, message = "WITHDRAW_AMOUNT_INVALID")
    private Long amount;
}
