
package com.epia.web;

import com.epia.domain.LifecycleChecklistRow;
import com.epia.domain.LifecycleFlowChart;
import com.epia.domain.LifecycleFlowTable;
import com.epia.domain.ProcessingTask;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.LifecycleChecklistDetailDto;
import com.epia.dto.ProcessingTaskDto;
import com.epia.repo.LifecycleChecklistRepo;
import com.epia.repo.LifecycleFlowChartRepo;
import com.epia.repo.LifecycleFlowTableRepo;
import com.epia.seq.SequenceService;
import com.epia.service.LifecycleService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lifecycle")
public class LifecycleController {

    private final LifecycleService svc;
    private final LifecycleFlowTableRepo ftRepo;
    private final LifecycleFlowChartRepo fcRepo;
    private final LifecycleChecklistRepo clRepo;
    private final SequenceService seq;

    public LifecycleController(
            LifecycleService svc,
            LifecycleFlowTableRepo ftr,
            LifecycleFlowChartRepo fcr,
            LifecycleChecklistRepo clr,
            SequenceService s
    ) {
        this.svc = svc;
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
    record FlowTablePayload(String companyId, Map<String, Object> data) {
    }

    @PostMapping("/flowtables")
    public Map<String, Object> saveFlowTable(@RequestBody FlowTablePayload p) {
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
    public List<LifecycleFlowChart> flowCharts(@RequestParam String companyId) {
        return fcRepo.findByCompanyId(companyId);
    }

    @PostMapping("/flowcharts")
    public Map<String, Object> saveFlowChart(@RequestBody LifecycleFlowChart body) {
        fcRepo.save(body);
        return Map.of(body.taskName, Map.of("nodes", body.nodes, "links", body.links));
    }

    // ===== Task =====
    @GetMapping("/tasks")
    public List<ProcessingTaskDto> tasks(@RequestParam String companyId) {
        System.out.println("GET /lifecycle/tasks - companyId: " + companyId);
        return svc.getTasks(companyId).stream()
                .map(task -> new ProcessingTaskDto(task.id.toHexString(), task.taskName))
                .collect(Collectors.toList());
    }

    // ===== Checklist =====
    @GetMapping("/checklists")
    public List<LifecycleChecklistDetailDto> checklists(
            @RequestParam String companyId,
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) List<String> status) {
        System.out.println("GET /lifecycle/checklists - companyId: " + companyId + ", taskId: " + taskId);

        ObjectId taskObjectId = taskId != null ? new ObjectId(taskId) : null;
        return svc.getChecklistsWithDetails(companyId, taskObjectId, status);
    }

    @PostMapping("/checklists")
    public Map<String, String> saveChecklists(@RequestBody Map<String, Object> body) {
        String companyId = (String) body.get("companyId");
        String taskId = (String) body.get("taskId");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");

        System.out.println("POST /lifecycle/checklists - companyId: " + companyId + ", taskId: " + taskId);

        List<ChecklistItem> items = dataList.stream()
                .map(this::mapToChecklistItem)
                .collect(Collectors.toList());

        svc.saveChecklists(companyId, new ObjectId(taskId), items);
        return Map.of("message", "저장되었습니다");
    }

    private ChecklistItem mapToChecklistItem(Map<String, Object> map) {
        ChecklistItem item = new ChecklistItem();

        item.no = (String) map.get("no");
        item.status = (String) map.get("status");
        item.evidence = (String) map.get("evidence");
        @SuppressWarnings("unchecked")
        List<Object> files = (List<Object>) map.get("files");
        item.files = files != null ? files : new ArrayList<>();

        return item;
    }

    // ===== Action Plans =====
    @GetMapping("/action-plans")
    public Map<String, Object> getActionPlans(@RequestParam String companyId) {
        System.out.println("GET /lifecycle/action-plans - companyId: " + companyId);
        return svc.getActionPlansMap(companyId);
    }

    @PostMapping("/action-plans")
    public Map<String, String> saveActionPlans(@RequestBody Map<String, Object> body) {
        String companyId = (String) body.get("companyId");
        @SuppressWarnings("unchecked")
        Map<String, Object> actionPlans = (Map<String, Object>) body.get("actionPlans");

        System.out.println("POST /lifecycle/action-plans - companyId: " + companyId);

        svc.saveActionPlansFromMap(companyId, actionPlans);
        return Map.of("message", "저장되었습니다");
    }

    // ===== Improvements (ReadOnly) =====
    @GetMapping("/improvements")
    public Map<String, Object> getImprovements(@RequestParam String companyId) {
        System.out.println("GET /lifecycle/improvements - companyId: " + companyId);
        return svc.getImprovementsMap(companyId);
    }

    /**
     * 대시보드용: 모든 Task와 Checklist 반환
     */
    @GetMapping("/tasks-with-checklists")
    public List<Map<String, Object>> getTasksWithChecklists(@RequestParam String companyId) {
        System.out.println("GET /lifecycle/tasks-with-checklists - companyId: " + companyId);
        return svc.getTasks(companyId).stream()
                .map(task -> {
                    Map<String, Object> taskData = new java.util.HashMap<>();
                    taskData.put("taskId", task.id.toHexString());
                    taskData.put("taskName", task.taskName);
                    taskData.put("lifecycleChecklist", task.lifecycleChecklist != null ? task.lifecycleChecklist : new ArrayList<>());
                    return taskData;
                })
                .collect(Collectors.toList());
    }
}