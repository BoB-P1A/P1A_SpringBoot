package com.epia.web;

import com.epia.dto.AccountDto;
import com.epia.service.AccountService;
import com.epia.service.AccountService.AccountCreateRequest;
import com.epia.service.AccountService.AccountUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 모든 기업의 모든 계정 조회
     * GET /accounts
     */
    @GetMapping("/accounts")
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    /**
     * 계정 생성
     * POST /companies/{companyId}/accounts
     */
    @PostMapping("/companies/{companyId}/accounts")
    public ResponseEntity<AccountDto> createAccount(
            @PathVariable String companyId,
            @RequestBody AccountCreateRequest request) {
        AccountDto created = accountService.createAccount(companyId, request);
        return ResponseEntity.ok(created);
    }

    /**
     * 계정 수정
     * PUT /companies/{companyId}/accounts/{accountId}
     */
    @PutMapping("/companies/{companyId}/accounts/{accountId}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable String companyId,
            @PathVariable String accountId,
            @RequestBody AccountUpdateRequest request) {
        AccountDto updated = accountService.updateAccount(companyId, accountId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 계정 삭제
     * DELETE /companies/{companyId}/accounts/{accountId}
     */
    @DeleteMapping("/companies/{companyId}/accounts/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String companyId,
            @PathVariable String accountId) {
        accountService.deleteAccount(companyId, accountId);
        return ResponseEntity.noContent().build();
    }
}