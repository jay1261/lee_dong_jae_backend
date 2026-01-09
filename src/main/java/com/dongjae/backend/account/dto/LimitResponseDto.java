package com.dongjae.backend.account.dto;

import com.dongjae.backend.account.entity.AccountSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LimitResponseDto {
    private Long withdraw;
    private Long transfer;

    public LimitResponseDto(AccountSetting accountSetting){
        this.withdraw = accountSetting.getWithdrawLimit();
        this.transfer = accountSetting.getTransferLimit();
    }
}
