
package com.epia.service;

import com.epia.repo.CompanyRepo;
import com.epia.domain.Company;
import com.epia.dto.CompanyDto;
import com.epia.web.CompanyController.CompanyCreateRequest;
import com.epia.web.CompanyController.CompanyUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class CompanyService {
    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public List<CompanyDto> listCompanies() {
        return companyRepo.findAll().stream().map(CompanyDto::from).toList();
    }

    public Company get(String id) {
        return companyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + id));
    }

    public Company save(Company c) {
        return companyRepo.save(c);
    }

    @Transactional
    public Company createCompany(CompanyCreateRequest request) {
        Company company = new Company();
        company.name = request.getName();
        company.contactName = request.getContactName();
        company.contactPhone = request.getContactPhone();
        company.createdAt = Instant.now();
        company.updatedAt = Instant.now();

        // 빈 리스트 초기화
        company.accounts = new ArrayList<>();
        company.evaluationItems = new ArrayList<>();
        company.processingTasks = new ArrayList<>();
        company.technicalSystems = new ArrayList<>();
        company.securitySystems = new ArrayList<>();

        return companyRepo.save(company);
    }

    @Transactional
    public Company updateCompany(String id, CompanyUpdateRequest request) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + id));

        company.name = request.getName();
        company.contactName = request.getContactName();
        company.contactPhone = request.getContactPhone();
        company.updatedAt = Instant.now();

        return companyRepo.save(company);
    }

    @Transactional
    public void deleteCompany(String id) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + id));

        // 연결된 계정이 있는 경우 경고 로그
        if (company.accounts != null && !company.accounts.isEmpty()) {
            System.out.println("경고: 기업 '" + company.name + "'에 " + company.accounts.size() + "개의 계정이 연결되어 있습니다.");
        }

        companyRepo.deleteById(id);
    }
}