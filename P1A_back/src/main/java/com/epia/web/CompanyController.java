package com.epia.web;

import com.epia.dto.CompanyDto;
import com.epia.service.CompanyService;
import com.epia.domain.Company;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompanyDto> list() {
        return service.listCompanies();
    }

    @GetMapping("/{id}")
    public Company get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<CompanyDto> create(@Valid @RequestBody CompanyCreateRequest request) {
        Company created = service.createCompany(request);
        CompanyDto dto = CompanyDto.from(created);  // ← DTO 변환 추가
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> update(
            @PathVariable String id,
            @Valid @RequestBody CompanyUpdateRequest request) {
        Company updated = service.updateCompany(id, request);
        CompanyDto dto = CompanyDto.from(updated);  // ← DTO 변환 추가
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Request DTOs =====

    public static class CompanyCreateRequest {
        @jakarta.validation.constraints.NotBlank(message = "기업명은 필수입니다")
        private String name;
        private String contactName;
        private String contactPhone;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }

        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }

    public static class CompanyUpdateRequest {
        @jakarta.validation.constraints.NotBlank(message = "기업명은 필수입니다")
        private String name;
        private String contactName;
        private String contactPhone;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }

        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }
}