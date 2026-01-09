package com.dongjae.backend.account.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "account_daily_limits")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AccountDailyLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private Long withdrawAmount;

    @Column(nullable = false)
    private Long transferAmount;

    @Column(nullable = false)
    private LocalDate date;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static AccountDailyLimit createDefault(Account account, LocalDate today) {
        AccountDailyLimit accountDailyLimit = new AccountDailyLimit();
        accountDailyLimit.account = account;
        accountDailyLimit.date = today;
        accountDailyLimit.withdrawAmount = 0L;
        accountDailyLimit.transferAmount = 0L;

        return accountDailyLimit;
    }

    public void updateWithdrawAmount(Long newWithdraw){
        this.withdrawAmount = newWithdraw;
    }
    public void updateTransferAmount(Long newTransfer){
        this.transferAmount = newTransfer;
    }
}
