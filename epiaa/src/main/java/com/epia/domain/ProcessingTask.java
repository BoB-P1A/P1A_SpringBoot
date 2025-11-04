package com.epia.domain;

import com.epia.domain.embedded.*;
import java.util.*;

public class ProcessingTask {
  public String id;
  public String taskName;
  public List<FlowTableEntry> flowTable = new ArrayList<>();
  public List<FlowChartNode> flowChartNodes = new ArrayList<>();
  public List<FlowChartLink> flowChartLinks = new ArrayList<>();

  public List<ChecklistItem> lifecycleChecklist = new ArrayList<>();
  public List<Improvement> improvements = new ArrayList<>();
  public List<ActionPlan> actionPlans = new ArrayList<>();
}