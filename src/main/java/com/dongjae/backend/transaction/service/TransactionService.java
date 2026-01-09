package com.dongjae.backend.transaction.service;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.service.AccountService;
import com.dongjae.backend.transaction.dto.DepositRequestDto;
import com.dongjae.backend.transaction.dto.DepositResponseDto;
import com.dongjae.backend.transaction.entity.Transaction;
import com.dongjae.backend.transaction.repository.TransactionRepository;
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
}
