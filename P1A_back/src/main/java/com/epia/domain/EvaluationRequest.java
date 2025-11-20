package com.epia.domain;

import java.time.Instant;
import java.util.*;

public class EvaluationRequest {
  public String id;
  public String companyId;   // null 허용 (embedded)
  public String title;
  public String status;      // REQUESTED|IN_PROGRESS|DONE
  public String requestedBy; // account id
  public Instant createdAt;
  public Instant updatedAt;

  public List<Evaluation> evaluations = new ArrayList<>();
}