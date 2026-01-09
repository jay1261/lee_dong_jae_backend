package com.dongjae.backend.transaction.controller;

import com.dongjae.backend.common.enums.SuccessType;
import com.dongjae.backend.common.response.BaseResponse;
import com.dongjae.backend.transaction.dto.DepositRequestDto;
import com.dongjae.backend.transaction.dto.DepositResponseDto;
import com.dongjae.backend.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/accounts/{accountNumber}/transactions/deposit")
    public ResponseEntity<BaseResponse<DepositResponseDto>> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequestDto request
    ){
        DepositResponseDto responseDto = transactionService.deposit(accountNumber, request);
        SuccessType successType = SuccessType.DEPOSIT_SUCCESS;

        BaseResponse<DepositResponseDto> response = new BaseResponse<>(
                successType.getHttpStatus().value(),
                successType.getMessage(),
                responseDto
        );

        return ResponseEntity.status(successType.getHttpStatus().value())
                .body(response);
    }
}
