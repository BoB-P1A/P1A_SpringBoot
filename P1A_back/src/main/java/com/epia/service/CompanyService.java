package com.epia.service;

import com.epia.repo.CompanyRepo;
import com.epia.domain.Company;
import com.epia.dto.CompanyDto;
import org.springframework.stereotype.Service;

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
    return companyRepo.findById(id).orElseThrow();
  }

  public Company save(Company c) {
    return companyRepo.save(c);
  }
}