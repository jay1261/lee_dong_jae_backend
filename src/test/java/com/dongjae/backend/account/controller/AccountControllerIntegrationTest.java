package com.dongjae.backend.account.controller;

import com.dongjae.backend.account.entity.Account;
import com.dongjae.backend.account.entity.AccountPolicy;
import com.dongjae.backend.account.entity.AccountSetting;
import com.dongjae.backend.account.repository.AccountPolicyRepository;
import com.dongjae.backend.account.repository.AccountRepository;
import com.dongjae.backend.account.repository.AccountSettingRepository;

import com.dongjae.backend.common.enums.AccountStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerIntegrationTest {
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


    @Test
    @DisplayName("계좌 생성 API 통합 테스트 - 성공")
    void createAccount_success() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpCode").value(201))
                .andExpect(jsonPath("$.message").value("계좌 등록에 성공했습니다."))
                .andExpect(jsonPath("$.data.accountNumber").exists())
                .andExpect(jsonPath("$.data.limits.withdraw").value(1000000))
                .andExpect(jsonPath("$.data.limits.transfer").value(3000000))
                .andReturn();
    }

    @Test
    @DisplayName("계좌 삭제 API 통합 테스트 - 성공")
    void deleteAccount_success() throws Exception {
        // given: 계좌 생성
        AccountPolicy policy = accountPolicyRepository.findByPolicyName("BASIC").get();
        String accountNumber = "20260109-00000001";
        Account account = Account.create(policy, accountNumber);
        accountRepository.save(account);
        AccountSetting setting = AccountSetting.createDefault(account);
        accountSettingRepository.save(setting);

        // when & then
        mockMvc.perform(patch("/api/accounts/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpCode").value(200))
                .andExpect(jsonPath("$.message").value("계좌 삭제에 성공했습니다."));

        // DB 검증
        Account closedAccount = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        assertThat(closedAccount.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    @DisplayName("계좌 삭제 API 통합 테스트 - 실패 (계좌 존재하지 않음)")
    void deleteAccount_fail_notFound() throws Exception {
        String nonExistentAccount = "20260109-99999999";

        mockMvc.perform(patch("/api/accounts/{accountNumber}", nonExistentAccount)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpCode").value(404))
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 계좌번호입니다."));
    }   
}
