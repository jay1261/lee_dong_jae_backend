package com.dongjae.backend.transaction.dto;

import com.dongjae.backend.transaction.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionHistoryResponseDto {
    private String transactionType;
    private String counterpartyAccountNumber;
    private Long amount;
    private Long fee;
    private Long balance;
    private LocalDateTime createdAt;

    public TransactionHistoryResponseDto (Transaction transaction) {
        this.transactionType = transaction.getTransactionType().name();
        this.counterpartyAccountNumber = transaction.getCounterpartyAccount() != null
                ? transaction.getCounterpartyAccount().getAccountNumber()
                : null;
        this.amount = transaction.getAmount();
        this.fee = transaction.getFee();
        this.balance = transaction.getBalanceAfter();
        this.createdAt = transaction.getCreatedAt();
    }
}
