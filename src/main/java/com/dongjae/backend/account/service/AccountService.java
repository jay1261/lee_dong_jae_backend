package com.dongjae.backend.account.service;

import com.dongjae.backend.account.dto.AccountDetailResponseDto;
import com.dongjae.backend.account.dto.AccountResponseDto;
import com.dongjae.backend.account.dto.AccountSummaryResponseDto;
import com.dongjae.backend.account.dto.LimitResponseDto;
import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountDailyLimit;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.account.entity.AccountSetting;
import com.dongjae.backend.account.repository.AccountDailyLimitRepository;
import com.dongjae.backend.account.repository.AccountPolicyRepository;
import com.dongjae.backend.account.repository.AccountRepository;
import com.dongjae.backend.account.repository.AccountSettingRepository;
import com.dongjae.backend.common.enums.AccountStatus;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.exception.CustomException;
import com.dongjae.backend.common.response.PageResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountPolicyRepository accountPolicyRepository;
    private final AccountSettingRepository accountSettingRepository;
    private final AccountDailyLimitRepository accountDailyLimitRepository;
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

    /**
     * 계좌 단일 조회
     * @param accountNumber 조회할 계좌번호
     * @return 계좌번호, 잔액, 상태, 한도 정보
     */
    public AccountDetailResponseDto getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomException(ErrorType.ACCOUNT_NOT_FOUND));

        AccountSetting setting = accountSettingRepository.findByAccount(account)
                .orElseThrow(() -> new CustomException(ErrorType.ACCOUNT_SETTING_NOT_FOUND));

        return new AccountDetailResponseDto(
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus(),
                new LimitResponseDto(setting)
        );
    }

    /**
     * 계좌 전체 조회
     * @param page page 번호
     * @param size size 크기
     * @return 계좌번호, 상태 리스트, 페이지 정보
     */
    @Transactional(readOnly = true)
    public PageResponse<AccountSummaryResponseDto> getAllAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Account> accounts = accountRepository.findAll(pageable);

        List<AccountSummaryResponseDto> content = accounts.stream()
                .map(AccountSummaryResponseDto::new)
                .toList();

        return new PageResponse<>(
                content,
                accounts.getNumber() + 1, // PageRequest는 0부터 시작하므로 +1
                accounts.getSize(),
                accounts.getTotalElements(),
                accounts.getTotalPages()
        );
    }

    /**
     * 출금/이체 전 계좌 잔액 및 일일 한도 체크 후 업데이트
     */
    @Transactional
    public void checkAndUpdateDailyLimit(Account account, Long amount, boolean isWithdraw) {

        // 잔액 확인
        if (account.getBalance() < amount) {
            throw new CustomException(ErrorType.INSUFFICIENT_BALANCE);
        }

        // 한도 조회
        AccountSetting setting = accountSettingRepository.findByAccount(account)
                .orElseThrow(() -> new CustomException(ErrorType.ACCOUNT_SETTING_NOT_FOUND));

        LocalDate today = LocalDate.now();
        AccountDailyLimit dailyLimit = accountDailyLimitRepository
                .findByAccountAndDate(account, today)
                .orElseGet(() -> AccountDailyLimit.createDefault(account, today));

        if (isWithdraw) {
            long newWithdraw = dailyLimit.getWithdrawAmount() + amount;
            if (newWithdraw > setting.getWithdrawLimit()) {
                throw new CustomException(ErrorType.WITHDRAW_LIMIT_EXCEEDED);
            }
            dailyLimit.updateWithdrawAmount(newWithdraw);
        } else {
            long newTransfer = dailyLimit.getTransferAmount() + amount;
            if (newTransfer > setting.getTransferLimit()) {
                throw new CustomException(ErrorType.TRANSFER_LIMIT_EXCEEDED);
            }
            dailyLimit.updateTransferAmount(newTransfer);
        }

        // DailyLimit 저장
        accountDailyLimitRepository.save(dailyLimit);
    }

    public Account getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                ()-> new CustomException(ErrorType.ACCOUNT_NOT_FOUND)
        );

        if(!account.getStatus().equals(AccountStatus.ACTIVE)){
            throw new CustomException(ErrorType.ACCOUNT_CLOSED);
        }

        return account;
    }

    public void updateBalance(Account account, Long amount) {
        account.updateBalance(amount);
    }

    public Long getAccountId(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                ()-> new CustomException(ErrorType.ACCOUNT_NOT_FOUND)
        );

        return account.getId();
    }
}
