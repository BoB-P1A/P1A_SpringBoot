package com.epia.repo;

import com.epia.domain.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CompanyRepo extends MongoRepository<Company, String> {

	@Query(value = "{ 'accounts.loginId': ?0 }", fields = "{ 'accounts.$': 1, 'name': 1 }")
	Optional<Company> findByAccountLoginId(String loginId); // 로그인 아이디
	Optional<Company> findById(String id); // companyId
}