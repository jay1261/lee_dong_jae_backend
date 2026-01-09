package com.dongjae.backend.transaction.dto;

import com.dongjae.backend.common.enums.ErrorType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositRequestDto {
    @NotNull(message = "DEPOSIT_AMOUNT_REQUIRED")
    @Min(value = 1, message = "DEPOSIT_AMOUNT_INVALID")
    private Long amount;
}