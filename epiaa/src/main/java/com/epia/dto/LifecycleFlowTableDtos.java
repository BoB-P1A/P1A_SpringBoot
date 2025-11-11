package com.epia.dto;

import lombok.Data;
import java.util.List;

// 흐름표 전체 응답
@Data
public class LifecycleFlowTableResponse {
    private String taskName;
    private CollectionData collection;
    private StorageData storage;
    private UsageData usage;
    private ProvisionData provision;
    private DisposalData disposal;
}

// 수집 단계 데이터
@Data
class CollectionData {
    private String id;
    private String detailTask;
    private String collectionStart;
    private String collectionPath;
    private String collectionSystem;
    private String collectionItem;
    private String collectionItemName;
    private String collectionMethod;
    private String collectionDepartment;
    private String isOnline;
    private String isEncrypted;
}

// 보유 단계 데이터
@Data
class StorageData {
    private String id;
    private String detailTask;
    private String storageSpace;
    private String collectionSystem;
    private String storageItem;
    private String storageItemName;
    private String storagePurpose;
    private String storageFormat;
    private String encryptionItem;
    private String isOnline;
    private String isEncrypted;
}

// 이용 단계 데이터
@Data
class UsageData {
    private String id;
    private String detailTask;
    private String storageSpace;
    private String usageSystem;
    private String usageItem;
    private String usageItemName;
    private String usagePurpose;
    private String usageMethod;
    private String usageDepartment;
    private String isOnline;
    private String isEncrypted;
}

// 제공 단계 데이터
@Data
class ProvisionData {
    private String id;
    private String detailTask;
    private String storageSpace;
    private String provisionSystem;
    private String provisionDepartment;
    private String recipient;
    private String provisionItem;
    private String provisionItemName;
    private String provisionPurpose;
    private String provisionMethod;
    private String linkageSystemEncrypted;
    private String recipientOnline;
    private String recipientEncrypted;
}

// 파기 단계 데이터
@Data
class DisposalData {
    private String id;
    private String detailTask;
    private String storageSpace;
    private String disposalSystem;
    private String disposalItem;
    private String disposalItemName;
    private String retentionPeriod;
    private String disposalDepartment;
    private String disposalProcedure;
    private String disposalOnline;
}

// 흐름표 저장 요청
@Data
class LifecycleFlowTableSaveRequest {
    private String companyId;
    private LifecycleFlowTableData data;
}

@Data
class LifecycleFlowTableData {
    private String taskName;
    private List<CollectionData> collection;
    private List<StorageData> storage;
    private List<UsageData> usage;
    private List<ProvisionData> provision;
    private List<DisposalData> disposal;
}

// 흐름표 저장 응답
@Data
class LifecycleFlowTableSaveResponse {
    private String taskName;
    private List<CollectionData> collection;
    private List<StorageData> storage;
    private List<UsageData> usage;
    private List<ProvisionData> provision;
    private List<DisposalData> disposal;
}