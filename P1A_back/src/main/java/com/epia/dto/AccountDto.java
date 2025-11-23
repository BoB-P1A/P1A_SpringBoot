
package com.epia.dto;

import com.epia.domain.Account;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AccountDto {
    public String _id;  // 프론트엔드에서 _id로 사용
    public String loginId;
    public String name;
    public String role;
    public String companyId;
    public String companyName;
    public String createdAt;
    public String updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("Asia/Seoul"));

    public static AccountDto from(Account account, String companyId, String companyName) {
        AccountDto dto = new AccountDto();
        dto._id = account.id;
        dto.loginId = account.loginId != null ? account.loginId : "";
        dto.name = account.name != null ? account.name : "";
        dto.role = account.role != null ? account.role : "";
        dto.companyId = companyId;
        dto.companyName = companyName != null ? companyName : "";
        dto.createdAt = "";
        dto.updatedAt = "";
        return dto;
    }

    // Getters and Setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}