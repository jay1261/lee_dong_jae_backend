package com.dongjae.backend.account.controller;

import com.dongjae.backend.account.repository.AccountRepository;
import com.dongjae.backend.account.repository.AccountSettingRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}
