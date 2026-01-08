package com.dongjae.backend.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessType {
    ACCOUNT_CREATED(HttpStatus.CREATED, "계좌 등록에 성공했습니다."),
    ACCOUNT_DELETED(HttpStatus.OK, "계좌 삭제에 성공했습니다."),
    ACCOUNT_RETRIEVED(HttpStatus.OK, "계좌 조회에 성공했습니다."),
    ACCOUNT_LIST_RETRIEVED(HttpStatus.OK, "계좌 목록 조회에 성공했습니다."),

    DEPOSIT_SUCCESS(HttpStatus.CREATED, "입금에 성공하였습니다."),
    WITHDRAW_SUCCESS(HttpStatus.CREATED, "출금에 성공하였습니다."),
    TRANSFER_SUCCESS(HttpStatus.CREATED, "이체에 성공하였습니다."),
    TRANSACTION_HISTORY_RETRIEVED(HttpStatus.OK, "거래내역 조회에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
