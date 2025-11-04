package com.epia.web;

import com.epia.domain.Task;
import com.epia.repo.TaskRepo;
import com.epia.seq.SequenceService;
import com.epia.support.ApiException;
import org.springframework.web.bind.annotation.*;
import java.util.*; 

@RestController @RequestMapping
public class TaskController {
  private final TaskRepo repo;
  private final SequenceService seq;
  
  public TaskController(TaskRepo r,SequenceService s){ this.repo=r; this.seq=s; }

  // 목록: /tasks?companyId=
  @GetMapping("/tasks")
  public List<Task> list(@RequestParam String companyId){ return repo.findByCompanyId(companyId); }

  // 생성
  @PostMapping("/tasks")
  public Task create(@RequestBody Task t){
    if(t.companyId==null || t.taskName==null || t.purpose==null || t.personalInfo==null || t.department==null) throw new ApiException(400,"필수 누락");
    t.id = seq.next("tasks");
    return repo.save(t);
  }

  // 수정
  @PutMapping("/tasks/{id}")
  public Task update(@PathVariable Integer id,@RequestBody Task body){
    Task t = repo.findById(id).orElseThrow(()->new ApiException(404,"없음"));
    if(body.taskName!=null) t.taskName=body.taskName;
    if(body.purpose!=null) t.purpose=body.purpose;
    if(body.personalInfo!=null) t.personalInfo=body.personalInfo;
    if(body.department!=null) t.department=body.department;
    return repo.save(t);
  }

  // 삭제
  @DeleteMapping("/tasks/{id}") public Map<String,Object> delete(@PathVariable Integer id){ repo.deleteById(id); return Map.of(); }

  // 일괄 수정 PUT /tasks/bulk  [{id, taskName, ...}]
  @PutMapping("/tasks/bulk")
  public Map<String,Object> bulk(@RequestBody List<Task> list){
    int cnt=0; for(Task b:list){ var t=repo.findById(b.id).orElse(null); if(t==null) continue;
      if(b.taskName!=null) t.taskName=b.taskName; if(b.purpose!=null) t.purpose=b.purpose;
      if(b.personalInfo!=null) t.personalInfo=b.personalInfo; if(b.department!=null) t.department=b.department; repo.save(t); cnt++; }
    return Map.of("updatedCount",cnt);
  }

  // 처리업무 메뉴 조회: /lifecycle/tasks?companyId= → id, taskName 만
  @GetMapping("/lifecycle/tasks")
  public List<Map<String,Object>> menu(@RequestParam String companyId){
    var list = repo.findByCompanyId(companyId);
    return list.stream()
            .map(t -> Map.<String, Object>of(
                    "id", t.id,
                    "taskName", t.taskName
            ))
            .toList();  }
}