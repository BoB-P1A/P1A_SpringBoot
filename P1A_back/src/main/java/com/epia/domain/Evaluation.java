package com.epia.domain;

import com.epia.domain.embedded.*;
import java.time.Instant;
import java.util.*;

public class Evaluation {
  public String id;
  public String companyId;
  public String requestId;
  public String title;
  public Instant startedAt;
  public Instant completedAt;
  public Instant createdAt;
  public Instant updatedAt;

  // 처리업무표 단위 Lifecycle 체크리스트
  public List<ProcessingTask> processingTasks = new ArrayList<>();

  // 시스템별 체크리스트 (기술적/보안성)
  public List<TechnicalSystem> technicalSystems = new ArrayList<>();
  public List<SecuritySystem> securitySystems = new ArrayList<>();
}