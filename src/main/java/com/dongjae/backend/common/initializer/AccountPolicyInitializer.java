package com.dongjae.backend.common.initializer;

import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.account.repository.AccountPolicyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountPolicyInitializer {
    private final AccountPolicyRepository accountPolicyRepository;

    @PostConstruct
    public void init(){
        if(accountPolicyRepository.count() > 0){
            return;
        }

        accountPolicyRepository.save(new AccountPolicy(new BigDecimal("0.01"), "BASIC"));
    }
}
