package com.epia.service;

import com.epia.repo.CompanyRepo;
import com.epia.domain.Company;
import com.epia.domain.Account;
import com.epia.support.ApiException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {
  private final CompanyRepo companyRepo;
  private final BCryptPasswordEncoder encoder;

  public AuthService(CompanyRepo companyRepo, BCryptPasswordEncoder encoder) {
    this.companyRepo = companyRepo;
    this.encoder = encoder;
  }

  public Map<String,Object> login(String loginId, String rawPassword) {
    Company company = companyRepo.findByAccountsLoginId(loginId)
      .orElseThrow(() -> new ApiException(401, "계정을 찾을 수 없습니다."));

    Account acc = company.accounts.stream()
      .filter(a -> loginId.equals(a.loginId))
      .findFirst().orElseThrow(() -> new ApiException(401, "계정을 찾을 수 없습니다."));

    if (!encoder.matches(rawPassword, acc.passwordHash)) {
      throw new ApiException(401, "비밀번호가 올바르지 않습니다.");
    }
    Map<String,Object> result = new HashMap<>();
    result.put("companyId", company.id);
    result.put("accountId", acc.id);
    result.put("name", acc.name);
    result.put("role", acc.role);
    return result;
  }
}