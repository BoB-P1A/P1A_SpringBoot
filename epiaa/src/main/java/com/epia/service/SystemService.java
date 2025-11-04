package com.epia.service;

import com.epia.domain.TechnicalSystem;
import com.epia.repo.TechnicalSystemRepo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SystemService {

    private final TechnicalSystemRepo repo;

    public SystemService(TechnicalSystemRepo repo) {
        this.repo = repo;
    }

    public List<TechnicalSystem> list(String companyId) {
        return repo.findByCompanyId(companyId);
    }

    public void save(TechnicalSystem sys) {
        repo.save(sys);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}