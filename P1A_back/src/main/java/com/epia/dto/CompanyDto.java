
package com.epia.dto;

import com.epia.domain.Company;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CompanyDto {
    public String _id;
    public String id;
    public String name;
    public String contactName;
    public String contactPhone;
    public String createdAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("Asia/Seoul"));

    public static CompanyDto from(Company c) {
        CompanyDto d = new CompanyDto();
        d.id = c.id;
        d._id = c.id;
        d.name = c.name != null ? c.name : "";
        d.contactName = c.contactName != null ? c.contactName : "";
        d.contactPhone = c.contactPhone != null ? c.contactPhone : "";

        if (c.createdAt != null) {
            d.createdAt = FORMATTER.format(c.createdAt);
        } else {
            d.createdAt = "";
        }

        return d;
    }

    // Getters and Setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}