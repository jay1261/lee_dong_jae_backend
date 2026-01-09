package com.dongjae.backend.transaction.service;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.service.AccountService;
import com.dongjae.backend.common.enums.AccountStatus;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.exception.CustomException;
import com.dongjae.backend.transaction.dto.DepositRequestDto;
import com.dongjae.backend.transaction.dto.DepositResponseDto;
import com.dongjae.backend.transaction.dto.WithdrawRequestDto;
import com.dongjae.backend.transaction.dto.WithdrawResponseDto;
import com.dongjae.backend.transaction.entity.Transaction;
import com.dongjae.backend.transaction.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public DepositResponseDto deposit(String accountNumber, DepositRequestDto request) {
        Account account = accountService.getAccountByNumber(accountNumber);

        accountService.updateBalance(account, request.getAmount());

        Transaction transaction = Transaction.createDeposit(account, request.getAmount());
        transactionRepository.save(transaction);

        // 5. 응답 DTO 생성
        return new DepositResponseDto(
                transaction.getId(),
                account.getAccountNumber(),
                transaction.getAmount(),
                transaction.getBalanceAfter()
        );
    }

    /**
     * 계좌 출금
     * @param accountNumber 출금할 계좌 번호
     * @param request 출금 요청 DTO
     * @return 출금 결과 DTO
     */
    @Transactional
    public WithdrawResponseDto withdraw(String accountNumber, WithdrawRequestDto request) {
        Account account = accountService.getAccountByNumber(accountNumber);

        long amount = request.getAmount();
        accountService.checkAndUpdateDailyLimit(account, amount, true);
        accountService.updateBalance(account, -amount);

        Transaction transaction = Transaction.createWithdraw(account, amount);
        transactionRepository.save(transaction);

        return new WithdrawResponseDto(
                transaction.getId(),
                account.getAccountNumber(),
                transaction.getAmount(),
                transaction.getBalanceAfter()
        );
    }
}
