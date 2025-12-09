
package com.epia.service;

import com.epia.domain.Account;
import com.epia.domain.Company;
import com.epia.dto.AccountDto;
import com.epia.repo.CompanyRepo;
import com.epia.support.ApiException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Service
public class AccountService {

    private final CompanyRepo companyRepo;
    private final PasswordEncoder passwordEncoder;

    public AccountService(CompanyRepo companyRepo, PasswordEncoder passwordEncoder) {
        this.companyRepo = companyRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 모든 기업의 모든 계정 조회
     */
    public List<AccountDto> getAllAccounts() {
        List<Company> companies = companyRepo.findAll();
        List<AccountDto> result = new ArrayList<>();

        for (Company company : companies) {
            if (company.accounts != null) {
                for (Account account : company.accounts) {
                    result.add(AccountDto.from(account, company.id, company.name));
                }
            }
        }

        return result;
    }

    /**
     * 계정 생성
     */
    @Transactional
    public AccountDto createAccount(String companyId, AccountCreateRequest request) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "기업을 찾을 수 없습니다"));

        if (company.accounts == null) {
            company.accounts = new ArrayList<>();
        }

        // 아이디 중복 체크
        if (isLoginIdDuplicate(request.loginId, null)) {
            throw new ApiException(400, "이미 사용 중인 아이디입니다");
        }

        Account newAccount = new Account();
        newAccount.id = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        newAccount.loginId = request.loginId;
        newAccount.passwordHash = passwordEncoder.encode(request.passwordHash);
        newAccount.name = request.name;
        newAccount.role = request.role;
        newAccount.createdAt = Instant.now();
        newAccount.updatedAt = Instant.now();

        company.accounts.add(newAccount);
        companyRepo.save(company);

        return AccountDto.from(newAccount, company.id, company.name);
    }

    /**
     * 계정 수정 (기업 이동 지원)
     */
    @Transactional
    public AccountDto updateAccount(String oldCompanyId, String accountId, AccountUpdateRequest request) {
        // 1. 기존 기업에서 계정 찾기
        Company oldCompany = companyRepo.findById(oldCompanyId)
                .orElseThrow(() -> new ApiException(404, "기존 기업을 찾을 수 없습니다"));

        if (oldCompany.accounts == null) {
            throw new ApiException(404, "계정을 찾을 수 없습니다");
        }

        Account account = oldCompany.accounts.stream()
                .filter(a -> a.id.equals(accountId))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "계정을 찾을 수 없습니다"));

        // 2. 아이디 중복 체크 (자신 제외)
        if (!account.loginId.equals(request.loginId) && isLoginIdDuplicate(request.loginId, accountId)) {
            throw new ApiException(400, "이미 사용 중인 아이디입니다");
        }

        // 3. 계정 정보 업데이트
        account.loginId = request.loginId;
        account.name = request.name;
        account.role = request.role;
        account.updatedAt = Instant.now();

        // 비밀번호가 제공된 경우에만 업데이트
        if (request.passwordHash != null && !request.passwordHash.isEmpty()) {
            account.passwordHash = passwordEncoder.encode(request.passwordHash);
        }

        // 4. 기업 변경 여부 확인
        String newCompanyId = request.companyId;

        if (newCompanyId != null && !newCompanyId.equals(oldCompanyId)) {
            // 기업 이동: 기존 기업에서 제거하고 새 기업에 추가
            Company newCompany = companyRepo.findById(newCompanyId)
                    .orElseThrow(() -> new ApiException(404, "새 기업을 찾을 수 없습니다"));

            if (newCompany.accounts == null) {
                newCompany.accounts = new ArrayList<>();
            }

            // 기존 기업에서 제거
            oldCompany.accounts.removeIf(a -> a.id.equals(accountId));
            companyRepo.save(oldCompany);

            // 새 기업에 추가
            newCompany.accounts.add(account);
            companyRepo.save(newCompany);

            return AccountDto.from(account, newCompany.id, newCompany.name);
        } else {
            // 같은 기업 내에서 수정
            companyRepo.save(oldCompany);
            return AccountDto.from(account, oldCompany.id, oldCompany.name);
        }
    }

    /**
     * 계정 삭제
     */
    @Transactional
    public void deleteAccount(String companyId, String accountId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "기업을 찾을 수 없습니다"));

        if (company.accounts == null) {
            throw new ApiException(404, "계정을 찾을 수 없습니다");
        }

        boolean removed = company.accounts.removeIf(a -> a.id.equals(accountId));

        if (!removed) {
            throw new ApiException(404, "계정을 찾을 수 없습니다");
        }

        companyRepo.save(company);
    }

    /**
     * 아이디 중복 체크
     */
    private boolean isLoginIdDuplicate(String loginId, String excludeAccountId) {
        List<Company> companies = companyRepo.findAll();

        for (Company company : companies) {
            if (company.accounts != null) {
                for (Account account : company.accounts) {
                    if (account.loginId.equals(loginId) &&
                            (excludeAccountId == null || !account.id.equals(excludeAccountId))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // ===== Request DTOs =====

    public static class AccountCreateRequest {
        private String loginId;
        private String passwordHash;
        private String name;
        private String role;

        public String getLoginId() { return loginId; }
        public void setLoginId(String loginId) { this.loginId = loginId; }

        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class AccountUpdateRequest {
        private String loginId;
        private String passwordHash;
        private String name;
        private String role;
        private String companyId;

        public String getLoginId() { return loginId; }
        public void setLoginId(String loginId) { this.loginId = loginId; }

        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getCompanyId() { return companyId; }
        public void setCompanyId(String companyId) { this.companyId = companyId; }
    }

    /**
     * loginId와 companyId로 계정 조회
     */
    public Account findByLoginIdAndCompanyId(String loginId, String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElse(null);

        if (company == null || company.accounts == null) {
            return null;
        }

        return company.accounts.stream()
                .filter(account -> account.loginId.equals(loginId))
                .findFirst()
                .orElse(null);
    }
}