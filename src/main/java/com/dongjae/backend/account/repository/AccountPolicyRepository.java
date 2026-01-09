package com.dongjae.backend.account.repository;

import com.dongjae.backend.account.entity.AccountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountPolicyRepository extends JpaRepository<AccountPolicy, Long> {

    Optional<AccountPolicy> findByPolicyName(String policyName);
}
