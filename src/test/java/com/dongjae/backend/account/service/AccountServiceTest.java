package com.dongjae.backend.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.dongjae.backend.account.dto.AccountResponseDto;
import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.account.entity.AccountSetting;
import com.dongjae.backend.account.repository.AccountPolicyRepository;
import com.dongjae.backend.account.repository.AccountRepository;
import com.dongjae.backend.account.repository.AccountSettingRepository;
import com.dongjae.backend.common.enums.AccountStatus;
import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountPolicyRepository accountPolicyRepository;

    @Mock
    private AccountSettingRepository accountSettingRepository;

    @Test
    void 계좌_생성_성공() {
        // given
        AccountPolicy policy = new AccountPolicy(
                BigDecimal.valueOf(0.01),
                "BASIC"
        );

        when(accountPolicyRepository.findByPolicyName("BASIC"))
                .thenReturn(Optional.of(policy));

        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(accountSettingRepository.save(any(AccountSetting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        AccountResponseDto response = accountService.addAccount();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccountNumber()).isNotBlank();
    }

    @Test
    void 기본_계좌_정책이_없으면_예외_발생() {
        // given
        when(accountPolicyRepository.findByPolicyName("BASIC"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.addAccount())
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.DEFAULT_ACCOUNT_POLICY_NOT_FOUND.getMessage());
    }


    @Test
    void 계좌_삭제_성공() {
        // given
        AccountPolicy policy = new AccountPolicy(BigDecimal.ZERO, "BASIC"); // 더미 정책
        Account account = Account.create(policy, "20260109-00000001");

        given(accountRepository.findByAccountNumber("20260109-00000001"))
                .willReturn(Optional.of(account));

        // when
        accountService.deleteAccount("20260109-00000001");

        // then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    void 계좌_삭제_실패() {
        // given
        given(accountRepository.findByAccountNumber("20260109-00000001"))
                .willReturn(Optional.empty());

        //when then
        CustomException exception = assertThrows(CustomException.class,
                () -> accountService.deleteAccount("20260109-00000001"));

        assertThat(exception.getErrorType()).isEqualTo(ErrorType.ACCOUNT_NOT_FOUND);
    }
}