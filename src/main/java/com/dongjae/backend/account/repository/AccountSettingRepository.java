package com.dongjae.backend.account.repository;

import com.dongjae.backend.account.entity.AccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSettingRepository extends JpaRepository<AccountSetting, Long> {

}
