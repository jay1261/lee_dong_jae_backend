package com.dongjae.backend.account.controller;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.account.entity.AccountSetting;
import com.dongjae.backend.account.repository.AccountPolicyRepository;
import com.dongjae.backend.account.repository.AccountRepository;
import com.dongjae.backend.account.repository.AccountSettingRepository;

import com.dongjae.backend.transaction.dto.DepositRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountSettingRepository accountSettingRepository;

    @Autowired
    private AccountPolicyRepository accountPolicyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account;

    @BeforeEach
    void setUp() {
        // 기본 정책 생성
        AccountPolicy policy = new AccountPolicy(BigDecimal.ZERO, "BASIC");
        accountPolicyRepository.save(policy);

        // 계좌 생성
        account = Account.create(policy, "20260109-00000001");
        accountRepository.save(account);

        // 계좌 설정 생성
        AccountSetting setting = AccountSetting.createDefault(account);
        accountSettingRepository.save(setting);
    }

    @Test
    @DisplayName("입금 API 통합 테스트 - 성공")
    void deposit_success() throws Exception {
        DepositRequestDto requestDto = new DepositRequestDto(10000L);

        mockMvc.perform(post("/api/accounts/{accountNumber}/transactions/deposit", account.getAccountNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpCode").value(201))
                .andExpect(jsonPath("$.message").value("입금에 성공하였습니다."))
                .andExpect(jsonPath("$.data.accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.data.amount").value(10000))
                .andExpect(jsonPath("$.data.balance").value(10000)); // 입금 후 잔액
    }

    @Test
    @DisplayName("입금 API 통합 테스트 - 실패 (금액 0 이하)")
    void deposit_fail_invalidAmount() throws Exception {
        DepositRequestDto requestDto = new DepositRequestDto(0L);

        mockMvc.perform(post("/api/accounts/{accountNumber}/transactions/deposit", account.getAccountNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpCode").value(400))
                .andExpect(jsonPath("$.errorCode").value("DEPOSIT_AMOUNT_INVALID"))
                .andExpect(jsonPath("$.message").value("입금 금액은 0보다 커야 합니다."));
    }
}