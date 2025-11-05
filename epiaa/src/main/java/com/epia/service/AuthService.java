package com.epia.service;

import com.epia.repo.CompanyRepo;
import com.epia.domain.Company;
import com.epia.domain.Account;
import com.epia.support.ApiException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private final CompanyRepo companyRepo;
    private final PasswordEncoder encoder;

    public AuthService(CompanyRepo companyRepo, PasswordEncoder encoder) {
        this.companyRepo = companyRepo;
        this.encoder = encoder;
    }

    public Map<String,Object> login(String loginId, String rawPassword) {

        Company company = companyRepo.findByAccountLoginId(loginId)
            .orElseThrow(() -> new ApiException(401, "사용자 없음"));

        Account acc = company.accounts.stream()
            .filter(a -> loginId.equals(a.loginId))
            .findFirst()
            .orElseThrow(() -> new ApiException(401, "사용자 없음"));

        if (!rawPassword.equals(acc.passwordHash)) {
        	throw new ApiException(401, "비밀번호 불일치");
        }

        Map<String,Object> result = new HashMap<>();
        result.put("companyId", company.id);
        result.put("accountId", acc.id);
        result.put("username", acc.loginId);
        result.put("name", acc.name);
        result.put("role", acc.role);

        return result;
    }
}