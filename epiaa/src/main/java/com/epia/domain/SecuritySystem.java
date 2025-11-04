package com.epia.domain;

import com.epia.domain.embedded.*;
import java.util.*;

public class SecuritySystem {
  public String id;
  public String systemName;
  public List<ChecklistItem> securityChecklist = new ArrayList<>();
  public List<Improvement> improvements = new ArrayList<>();
  public List<ActionPlan> actionPlans = new ArrayList<>();
}