package com.dongjae.backend.account.repository;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountDailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

public interface AccountDailyLimitRepository extends JpaRepository<AccountDailyLimit, Long> {

    Optional<AccountDailyLimit> findByAccountAndDate(Account account, LocalDate date);
}
