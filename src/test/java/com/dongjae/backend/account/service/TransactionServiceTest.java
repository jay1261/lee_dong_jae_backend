package com.dongjae.backend.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;


import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.exception.CustomException;
import com.dongjae.backend.transaction.dto.DepositRequestDto;
import com.dongjae.backend.transaction.dto.DepositResponseDto;
import com.dongjae.backend.transaction.entity.Transaction;
import com.dongjae.backend.transaction.repository.TransactionRepository;
import com.dongjae.backend.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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

}