//package com.epia.service;
//
//import com.epia.domain.Company;
//import com.epia.domain.TechnicalSystem;
//import com.epia.repo.CompanyRepo;
//import com.epia.seq.SequenceService;
//import org.springframework.stereotype.Service;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class SystemService {
//
//    private final CompanyRepo companyRepo;
//    private final SequenceService seq;
//
//    public SystemService(CompanyRepo companyRepo, SequenceService seq) {
//        this.companyRepo = companyRepo;
//        this.seq = seq;
//    }
//
//    public List<TechnicalSystem> list(String companyId) {
//        Company company = companyRepo.findById(companyId)
//                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));
//
//        if (company.technicalSystems == null) {
//            company.technicalSystems = new ArrayList<>();
//        }
//        return company.technicalSystems;
//    }
//
//    public void save(TechnicalSystem sys) {
//        // sys 객체에 companyId가 포함되어 있어야 합니다
//        Company company = companyRepo.findById(sys.companyId)
//                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));
//
//        if (company.technicalSystems == null) {
//            company.technicalSystems = new ArrayList<>();
//        }
//
//        if (sys.id == null) {
//            sys.id = seq.next("technical_systems");
//        }
//
//        // 기존 시스템 업데이트 또는 새로 추가
//        boolean updated = false;
//        for (int i = 0; i < company.technicalSystems.size(); i++) {
//            if (company.technicalSystems.get(i).id.equals(sys.id)) {
//                company.technicalSystems.set(i, sys);
//                updated = true;
//                break;
//            }
//        }
//
//        if (!updated) {
//            company.technicalSystems.add(sys);
//        }
//
//        companyRepo.save(company);
//    }
//
//    public void delete(Integer id) {
//        List<Company> companies = companyRepo.findAll();
//
//        for (Company company : companies) {
//            if (company.technicalSystems != null) {
//                boolean removed = company.technicalSystems.removeIf(sys -> sys.id.equals(id));
//                if (removed) {
//                    companyRepo.save(company);
//                    return;
//                }
//            }
//        }
//
//        throw new RuntimeException("시스템을 찾을 수 없습니다.");
//    }
//}