package com.dongjae.backend.account.repository;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountSettingRepository extends JpaRepository<AccountSetting, Long> {

    Optional<AccountSetting> findByAccount(Account account);
}
