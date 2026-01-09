package com.dongjae.backend.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "account_policies")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AccountPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_rate", nullable = false)
    private BigDecimal feeRate;

    @Column(name = "policy_name", nullable = false, length = 50)
    private String policyName;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AccountPolicy(BigDecimal feeRate, String policyName){
        this.feeRate = feeRate;
        this.policyName = policyName;
    }
}
