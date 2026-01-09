package com.dongjae.backend.account.controller;

import com.dongjae.backend.account.dto.AccountResponseDto;
import com.dongjae.backend.account.service.AccountService;
import com.dongjae.backend.common.enums.SuccessType;
import com.dongjae.backend.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<BaseResponse<AccountResponseDto>> createAccount(){
        AccountResponseDto responseDto = accountService.addAccount();
        SuccessType successType = SuccessType.ACCOUNT_CREATED;

        BaseResponse<AccountResponseDto> response = new BaseResponse<>(
                successType.getHttpStatus().value(),
                successType.getMessage(),
                responseDto
        );

        return ResponseEntity.status(successType.getHttpStatus().value()).body(response);
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BaseResponse<Void>> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        SuccessType successType = SuccessType.ACCOUNT_DELETED;

        BaseResponse<Void> response = new BaseResponse<>(
                successType.getHttpStatus().value(),
                successType.getMessage(),
                null
        );

        return ResponseEntity.status(successType.getHttpStatus().value()).body(response);
    }

}
