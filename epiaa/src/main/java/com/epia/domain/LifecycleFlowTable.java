package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("lifecycle_flowtables")
public class LifecycleFlowTable {
    @Id
    public String id;
    public String companyId;
    public String taskName;

    public List<String> collection;
    public List<String> storage;
    public List<String> usage;
    public List<String> provision;
    public List<String> disposal;

    public static class Row {
        public String id;
        public String defaultTask;
        public String collectionTarget;
        public String collectionSystem;
        public String collectionPath;
        public String collectionPurpose;
        public String collectionDepartment;
        public String isOnline;
        public String isEncrypted;

        public String storageSpace;
        public String storageMethod;
        public String storagePurpose;
        public String storageDepartment;

        public String usageSystem;
        public String usagePattern;
        public String usagePurpose;
        public String usageMethod;
        public String usageDepartment;

        public String provisionSystem;
        public String provisionPath;
        public String provisionDepartment;
        public String recipientOnline;
        public String recipientEncrypted;

        public String disposalSystem;
        public String disposalMethod;
        public String retentionPeriod;
        public String disposalDepartment;
        public String disposalProcedure;
        public String disposalOnline;
    }
}