package com.dongjae.backend.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계좌번호입니다."),
    COUNTERPARTY_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "상대 계좌를 찾을 수 없습니다."),
    ACCOUNT_CLOSED(HttpStatus.CONFLICT, "해지된 계좌입니다."),
    ACCOUNT_BALANCE_REMAIN(HttpStatus.CONFLICT, "계좌에 잔액이 남아 있습니다."),
    DEFAULT_ACCOUNT_POLICY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "기본 계좌 정책이 존재하지 않습니다."),
    ACCOUNT_NUMBER_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "계좌번호 생성에 실패했습니다. 잠시 후 다시 시도해주세요."),

    TRANSFER_AMOUNT_REQUIRED(HttpStatus.BAD_REQUEST, "이체 금액을 입력해주세요."),
    TRANSFER_AMOUNT_INVALID(HttpStatus.BAD_REQUEST, "이체 금액은 0보다 커야 합니다."),
    TRANSFER_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "일일 이체 한도를 초과했습니다."),
    INSUFFICIENT_BALANCE(HttpStatus.CONFLICT, "잔액이 부족합니다."),

    WITHDRAW_AMOUNT_REQUIRED(HttpStatus.BAD_REQUEST, "출금 금액을 입력해주세요."),
    WITHDRAW_AMOUNT_INVALID(HttpStatus.BAD_REQUEST, "출금 금액은 0보다 커야 합니다."),
    WITHDRAW_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "일일 출금 한도를 초과했습니다."),

    DEPOSIT_AMOUNT_REQUIRED(HttpStatus.BAD_REQUEST, "입금 금액을 입력해주세요."),
    DEPOSIT_AMOUNT_INVALID(HttpStatus.BAD_REQUEST, "입금 금액은 0보다 커야 합니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
