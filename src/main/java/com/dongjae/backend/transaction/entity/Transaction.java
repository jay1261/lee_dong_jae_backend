package com.dongjae.backend.transaction.entity;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.common.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "transactions")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty_account_id")
    private Account counterpartyAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Long amount;

    @Column
    private Long fee;

    @Column(nullable = false)
    private Long balanceAfter;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Transaction createDeposit(Account account, Long amount) {
        Transaction transaction = new Transaction();
        transaction.account = account;
        transaction.amount = amount;
        transaction.balanceAfter = account.getBalance();
        transaction.counterpartyAccount = null;
        transaction.transactionType = TransactionType.DEPOSIT;
        transaction.fee = null;
        return transaction;
    }
}
