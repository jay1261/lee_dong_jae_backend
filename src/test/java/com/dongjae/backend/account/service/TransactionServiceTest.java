package com.dongjae.backend.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.enums.TransactionType;
import com.dongjae.backend.common.exception.CustomException;
import com.dongjae.backend.common.response.PageResponse;
import com.dongjae.backend.transaction.dto.*;
import com.dongjae.backend.transaction.entity.Transaction;
import com.dongjae.backend.transaction.repository.TransactionRepository;
import com.dongjae.backend.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TransactionServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void 입금_성공() {
        Account account = Account.create(new AccountPolicy(BigDecimal.ZERO, "BASIC"), "20260109-00000001");
        DepositRequestDto request = new DepositRequestDto(10000L);

        given(accountService.getAccountByNumber("20260109-00000001")).willReturn(account);
        doNothing().when(accountService).updateBalance(account, 10000L);
        given(transactionRepository.save(any(Transaction.class))).willAnswer(i -> i.getArgument(0));

        DepositResponseDto response = transactionService.deposit("20260109-00000001", request);

        assertThat(response.getAccountNumber()).isEqualTo("20260109-00000001");
        assertThat(response.getAmount()).isEqualTo(10000L);
        assertThat(response.getBalance()).isEqualTo(account.getBalance());
    }


    @Test
    void 출금_성공() {
        // given
        Account account = Account.create(new AccountPolicy(BigDecimal.ZERO, "BASIC"), "20260109-00000001");
        account.updateBalance(5_000_000L); // 초기 잔액

        WithdrawRequestDto request = new WithdrawRequestDto(1_000_000L);

        given(accountService.getAccountByNumber("20260109-00000001")).willReturn(account);
        doNothing().when(accountService).checkAndUpdateDailyLimit(account, 1_000_000L, true);
        given(transactionRepository.save(any(Transaction.class))).willAnswer(i -> i.getArgument(0));

        // when
        WithdrawResponseDto response = transactionService.withdraw("20260109-00000001", request);

        // then
        assertThat(response.getAccountNumber()).isEqualTo("20260109-00000001");
        assertThat(response.getAmount()).isEqualTo(1_000_000L);
        assertThat(response.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    void 출금_실패_한도초과() {
        Account account = Account.create(new AccountPolicy(BigDecimal.ZERO, "BASIC"), "20260109-00000001");
        account.updateBalance(5_000_000L);

        WithdrawRequestDto request = new WithdrawRequestDto(2_000_000L);

        given(accountService.getAccountByNumber("20260109-00000001")).willReturn(account);

        doThrow(new CustomException(ErrorType.WITHDRAW_LIMIT_EXCEEDED))
                .when(accountService).checkAndUpdateDailyLimit(account, 2_000_000L, true);

        assertThatThrownBy(() -> transactionService.withdraw("20260109-00000001", request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorType.WITHDRAW_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    void 이체_성공() {
        // given
        Account fromAccount = Account.create(new AccountPolicy(new BigDecimal("0.01"), "BASIC"), "20260109-00000001");
        fromAccount.updateBalance(10_000L);
        Account toAccount = Account.create(new AccountPolicy(new BigDecimal("0.01"), "BASIC"), "20260109-00000002");
        toAccount.updateBalance(5_000L);

        TransferRequestDto request = new TransferRequestDto(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                1_000L
        );

        given(accountService.getAccountByNumber(fromAccount.getAccountNumber())).willReturn(fromAccount);
        given(accountService.getAccountByNumber(toAccount.getAccountNumber())).willReturn(toAccount);
        doNothing().when(accountService).checkAndUpdateDailyLimit(fromAccount, 1_000L, false);
        doNothing().when(accountService).updateBalance(fromAccount, -1_010L); // 1000 + 수수료 10
        doNothing().when(accountService).updateBalance(toAccount, 1_000L);

        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        TransferResponseDto response = transactionService.transfer(request);

        // then
        assertThat(response.getFromAccountNumber()).isEqualTo(fromAccount.getAccountNumber());
        assertThat(response.getToAccountNumber()).isEqualTo(toAccount.getAccountNumber());
        assertThat(response.getAmount()).isEqualTo(1000L);
        assertThat(response.getFee()).isEqualTo(10L); // fee = 1000 * 0.01
    }

    @Test
    void 이체_실패_잔액부족() {
        // given
        Account fromAccount = Account.create(new AccountPolicy(new BigDecimal("0.01"), "BASIC"), "20260109-00000001");
        fromAccount.updateBalance(500L); // 잔액 부족
        Account toAccount = Account.create(new AccountPolicy(new BigDecimal("0.01"), "BASIC"), "20260109-00000002");

        TransferRequestDto request = new TransferRequestDto(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                1_000L
        );

        given(accountService.getAccountByNumber(fromAccount.getAccountNumber())).willReturn(fromAccount);
        given(accountService.getAccountByNumber(toAccount.getAccountNumber())).willReturn(toAccount);
        doNothing().when(accountService).checkAndUpdateDailyLimit(fromAccount, 1_000L, false);

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorType.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    void 거래내역_조회_성공(){
        // given
        String accountNumber = "20260109-00000001";
        Long accountId = 1L;
        int page = 1;
        int size = 2;

        when(accountService.getAccountId(accountNumber))
                .thenReturn(accountId);

        Transaction tx1 = mock(Transaction.class);
        Transaction tx2 = mock(Transaction.class);

        when(tx1.getTransactionType()).thenReturn(TransactionType.WITHDRAW);
        when(tx2.getTransactionType()).thenReturn(TransactionType.DEPOSIT);

        when(tx1.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(tx2.getCreatedAt()).thenReturn(LocalDateTime.now().minusMinutes(10));

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(tx1, tx2),
                PageRequest.of(0, size),
                2
        );

        when(transactionRepository.findByAccountId(
                eq(accountId),
                any(Pageable.class))
        ).thenReturn(transactionPage);

        // when
        PageResponse<TransactionHistoryResponseDto> result =
                transactionService.getTransactions(accountNumber, page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

}