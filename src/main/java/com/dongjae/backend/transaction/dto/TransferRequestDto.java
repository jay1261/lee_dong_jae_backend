package com.dongjae.backend.transaction.dto;

import com.dongjae.backend.common.enums.ErrorType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferRequestDto {
    @NotBlank(message = "ACCOUNT_NUMBER_REQUIRED")
    private String fromAccountNumber;

    @NotBlank(message = "COUNTERPARTY_ACCOUNT_NUMBER_REQUIRED")
    private String toAccountNumber;

    @NotNull(message = "TRANSFER_AMOUNT_REQUIRED")
    @Min(value = 1, message = "TRANSFER_AMOUNT_INVALID")
    private Long amount;
}
