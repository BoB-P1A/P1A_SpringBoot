package com.epia.dto;

import com.epia.domain.Company;
import java.time.Instant;

public class CompanyDto {
  public String id;
  public String name;
  public String contactName;
  public String contactPhone;
  public Instant createdAt;
  public Instant updatedAt;

  public static CompanyDto from(Company c) {
    CompanyDto d = new CompanyDto();
    d.id = c.id; d.name = c.name; d.contactName = c.contactName; d.contactPhone = c.contactPhone;
    d.createdAt = c.createdAt; d.updatedAt = c.updatedAt;
    return d;
  }
}