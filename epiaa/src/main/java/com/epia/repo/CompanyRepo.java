package com.epia.repo;

import com.epia.domain.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyRepo extends MongoRepository<Company, String> {
    // 로그인 시 사용 (accounts 배열 내부에서 loginId 매칭)
    Optional<Company> findByAccountsLoginId(String loginId);
}