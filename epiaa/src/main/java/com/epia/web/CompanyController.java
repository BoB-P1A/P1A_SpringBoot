package com.epia.web;

import com.epia.dto.CompanyDto;
import com.epia.service.CompanyService;
import com.epia.domain.Company;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
  private final CompanyService service;
  public CompanyController(CompanyService service) { this.service = service; }

  @GetMapping
  public List<CompanyDto> list() {
    return service.listCompanies();
  }

  @GetMapping("/{id}")
  public Company get(@PathVariable String id) {
    return service.get(id);
  }

  @PostMapping
  public Company create(@RequestBody Company c) {
    return service.save(c);
  }
}