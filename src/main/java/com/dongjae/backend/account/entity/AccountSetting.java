package com.dongjae.backend.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "account_settings")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AccountSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false)
    private Long withdrawLimit;

    @Column(nullable = false)
    private Long transferLimit;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static AccountSetting createDefault(Account account) {
        AccountSetting setting = new AccountSetting();
        setting.account = account;
        setting.withdrawLimit = 1_000_000L;
        setting.transferLimit = 3_000_000L;
        return setting;
    }
}
