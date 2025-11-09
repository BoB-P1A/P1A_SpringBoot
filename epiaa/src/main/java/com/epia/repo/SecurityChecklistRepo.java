//package com.epia.repo;
//
//import com.epia.domain.SecurityChecklistRow;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import java.util.List;
//
//public interface SecurityChecklistRepo extends MongoRepository<SecurityChecklistRow, Integer> {
//    List<SecurityChecklistRow> findByCompanyId(String companyId);
//    List<SecurityChecklistRow> findByCompanyIdAndTargetName(String companyId, String targetName);
//}