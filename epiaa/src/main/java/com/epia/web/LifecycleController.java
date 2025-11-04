package com.epia.web;

import com.epia.domain.LifecycleChecklistRow;
import com.epia.domain.LifecycleFlowChart;
import com.epia.domain.LifecycleFlowTable;
import com.epia.repo.LifecycleChecklistRepo;
import com.epia.repo.LifecycleFlowChartRepo;
import com.epia.repo.LifecycleFlowTableRepo;
import com.epia.seq.SequenceService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/lifecycle")
public class LifecycleController {

  private final LifecycleFlowTableRepo ftRepo;
  private final LifecycleFlowChartRepo fcRepo;
  private final LifecycleChecklistRepo clRepo;
  private final SequenceService seq;

  public LifecycleController(
      LifecycleFlowTableRepo ftr,
      LifecycleFlowChartRepo fcr,
      LifecycleChecklistRepo clr,
      SequenceService s
  ) {
    this.ftRepo = ftr;
    this.fcRepo = fcr;
    this.clRepo = clr;
    this.seq = s;
  }

  // =========================
  // 흐름표 목록
  // =========================
  @GetMapping("/flowtables")
  public List<LifecycleFlowTable> flowTables(@RequestParam String companyId) {
    return ftRepo.findByCompanyId(companyId);
  }

  // 흐름표 저장
  record FlowTablePayload(String companyId, Map<String,Object> data){}
  @PostMapping("/flowtables")
  public Map<String,Object> saveFlowTable(@RequestBody FlowTablePayload p){
      LifecycleFlowTable t = new LifecycleFlowTable();
      t.companyId = p.companyId;

      // taskName = JSON 최상위 key
      String taskName = p.data.keySet().iterator().next();
      t.taskName = taskName;

      // 특정 taskName 에 대한 블록들
      Map<String, Object> blocks = (Map<String, Object>) p.data.get(taskName);

      // 각 단계별 리스트 추출 (List<String> 형식 가정)
      t.collection = (List<String>) blocks.get("collection");
      t.storage = (List<String>) blocks.get("storage");
      t.usage = (List<String>) blocks.get("usage");
      t.provision = (List<String>) blocks.get("provision");
      t.disposal = (List<String>) blocks.get("disposal");

      ftRepo.save(t);

      return Map.of(
          taskName, Map.of(
              "collection", t.collection,
              "storage", t.storage,
              "usage", t.usage,
              "provision", t.provision,
              "disposal", t.disposal
          )
      );
  }

  // =========================
  // 흐름도 목록/저장
  // =========================
  @GetMapping("/flowcharts")
  public List<LifecycleFlowChart> flowCharts(@RequestParam String companyId){
    return fcRepo.findByCompanyId(companyId);
  }

  @PostMapping("/flowcharts")
  public Map<String,Object> saveFlowChart(@RequestBody LifecycleFlowChart body){
    fcRepo.save(body);
    return Map.of(body.taskName, Map.of("nodes", body.nodes, "links", body.links));
  }

  // =========================
  // 체크리스트 조회 (원본 유지 + taskName 필터 추가)
  // =========================
  @GetMapping("/lifecycle")
  public List<LifecycleChecklistRow> lifecycleList(
      @RequestParam String companyId,
      @RequestParam(required = false) String taskName // ★ 추가
  ){
    if (taskName == null || taskName.isBlank()) {  // ★ 추가
      return clRepo.findByCompanyId(companyId);
    }
    return clRepo.findByCompanyIdAndTaskName(companyId, taskName); // ★ 추가
  }

  // =========================
  // 체크리스트 저장 (업서트 방식, 원본 유지 + 수정)
  // =========================
  @PostMapping("/lifecycle")
  public Map<String,String> saveLifecycle(@RequestBody Map<String,Object> b){
    String companyId=(String)b.get("companyId");
    String taskName=(String)b.get("taskName");
    List<Map<String,Object>> data=(List<Map<String,Object>>) b.get("data");

    for(Map<String,Object> m : data){

      Integer id = (m.get("id") instanceof Number) ? ((Number)m.get("id")).intValue() : null; // ★ NEW

      LifecycleChecklistRow r = (id == null)
          ? new LifecycleChecklistRow()
          : clRepo.findById(id).orElse(new LifecycleChecklistRow()); // ★ NEW

      if (r.id == null) r.id = seq.next("lifecycle_checklists"); // ★ NEW
      r.companyId=companyId;
      r.taskName=taskName;
      r.field=(String)m.get("field");
      r.subField=(String)m.get("subField");
      r.no=(String)m.get("no");
      r.item=(String)m.get("item");
      r.status=(String)m.get("status");
      r.evidence=(String)m.get("evidence");

      r.files = (List<Object>) m.get("files"); // ★ 유지

      clRepo.save(r);
    }
    return Map.of("message","체크리스트가 저장되었습니다");
  }
}