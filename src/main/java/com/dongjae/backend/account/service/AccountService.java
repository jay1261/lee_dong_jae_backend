package com.dongjae.backend.account.service;

import com.dongjae.backend.account.dto.AccountResponseDto;
import com.dongjae.backend.account.dto.LimitResponseDto;
import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.account.entity.AccountSetting;
import com.dongjae.backend.account.repository.AccountPolicyRepository;
import com.dongjae.backend.account.repository.AccountRepository;
import com.dongjae.backend.account.repository.AccountSettingRepository;
import com.dongjae.backend.common.enums.AccountStatus;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountPolicyRepository accountPolicyRepository;
    private final AccountSettingRepository accountSettingRepository;

    /**
     * 신규 계좌 생성
     * @return 생성된 계좌번호와 출금/이체 한도 정보
     */
    @Transactional
    public AccountResponseDto addAccount() {
        // 현재는 BASIC으로 고정, 추후 확장가능
        AccountPolicy accountPolicy = accountPolicyRepository.findByPolicyName("BASIC").orElseThrow(
                () -> new CustomException(ErrorType.DEFAULT_ACCOUNT_POLICY_NOT_FOUND)
        );

        // Account 생성
        String accountNumber = generateUniqueAccountNumber();
        Account account = Account.create(accountPolicy, accountNumber);
        accountRepository.save(account);

        AccountSetting accountSetting = AccountSetting.createDefault(account);
        accountSettingRepository.save(accountSetting);

        return new AccountResponseDto(accountNumber, new LimitResponseDto(accountSetting));
    }

    /**
     * accountNumber를 unique하게 생성하도록 재시도
     * @return unique하게 생성된 account number
     */
    private String generateUniqueAccountNumber() {
        for (int i = 0; i < 10; i++) {
            String accountNumber = generateAccountNumber();
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
        throw new CustomException(ErrorType.ACCOUNT_NUMBER_GENERATION_FAILED);
    }

    /**
     * 날짜, 랜덤숫자 기반으로 계좌번호 생성.
     * @return 20260109-00000001 형태의 계좌번호
     */
    private String generateAccountNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100_000_000));
        return date + "-" + random;
    }

    /**
     * 계좌 삭제 (상태 CLOSED로 변경)
     * @param accountNumber 삭제할 계좌번호
     */
    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new CustomException(ErrorType.ACCOUNT_NOT_FOUND)
        );

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new CustomException(ErrorType.ACCOUNT_CLOSED);
        }

        if (account.getBalance() > 0) {
            throw new CustomException(ErrorType.ACCOUNT_BALANCE_REMAIN);
        }

        account.updateStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }
}
