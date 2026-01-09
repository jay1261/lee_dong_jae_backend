package com.dongjae.backend.transaction.service;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.service.AccountService;
import com.dongjae.backend.common.enums.AccountStatus;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.enums.TransactionType;
import com.dongjae.backend.common.exception.CustomException;
import com.dongjae.backend.common.response.PageResponse;
import com.dongjae.backend.transaction.dto.*;
import com.dongjae.backend.transaction.entity.Transaction;
import com.dongjae.backend.transaction.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 이체
     * @param request 이체 요청 DTO
     * @return 이체 결과 DTO
     */
    @Transactional
    public TransferResponseDto transfer(TransferRequestDto request) {
        // 계좌 조회
        Account fromAccount = accountService.getAccountByNumber(request.getFromAccountNumber());
        Account toAccount = accountService.getAccountByNumber(request.getToAccountNumber());

        // 일일 한도 체크
        Long amount = request.getAmount();
        accountService.checkAndUpdateDailyLimit(fromAccount, amount, false);

        // 수수료 계산
        BigDecimal feeRate = fromAccount.getAccountPolicy().getFeeRate();
        long fee = BigDecimal.valueOf(amount).multiply(feeRate).longValue();

        long totalDeduct = amount + fee;

        // 수수료 포함 잔액 계산
        if(fromAccount.getBalance() < totalDeduct){
            throw new CustomException(ErrorType.INSUFFICIENT_BALANCE);
        }

        accountService.updateBalance(fromAccount, -totalDeduct);
        accountService.updateBalance(toAccount, amount);

        Transaction fromTransaction = Transaction.createTransfer(fromAccount, toAccount, amount, fee, TransactionType.TRANSFER_OUT);
        Transaction toTransaction = Transaction.createTransfer(toAccount, fromAccount, amount, null, TransactionType.TRANSFER_IN);
        transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);

        return new TransferResponseDto(
                fromTransaction.getId(),
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                fromTransaction.getAmount(),
                fromTransaction.getFee(),
                fromAccount.getBalance()
        );
    }

    /**
     * 거래 내역 조회
     * @param accountNumber 조회할 계좌번호
     * @param page page number
     * @param size page size
     * @return 거래내역 조회 결과 DTO
     */
    @Transactional(readOnly = true)
    public PageResponse<TransactionHistoryResponseDto> getTransactions(String accountNumber, int page, int size) {
        Long accountId = accountService.getAccountId(accountNumber);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);

        List<TransactionHistoryResponseDto> dtos = transactions.stream()
                .map(TransactionHistoryResponseDto::new)
                .toList();

        return new PageResponse<>(dtos, transactions.getNumber() + 1, transactions.getSize(), transactions.getTotalElements(), transactions.getTotalPages());
    }
}
