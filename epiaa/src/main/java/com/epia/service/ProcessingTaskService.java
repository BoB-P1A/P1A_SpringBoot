package com.epia.service;

import com.epia.domain.Company;
import com.epia.domain.ProcessingTask;
import com.epia.dto.ProcessingTaskDto;
import com.epia.repo.CompanyRepo;
import com.epia.support.ApiException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 처리업무표 페이지 전용 서비스
 * Company 도큐먼트의 processingTasks 배열을 직접 관리
 */
@Service
public class ProcessingTaskService {

    private final CompanyRepo companyRepo;

    public ProcessingTaskService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    /**
     * 회사별 처리업무 목록 조회
     */
    public List<ProcessingTaskDto> getAllTasksByCompanyId(String companyId) {
        Company company = companyRepo.findById(companyId).orElse(null);

        if (company == null || company.processingTasks == null) {
            return List.of();
        }

        return company.processingTasks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 처리업무 생성
     */
    public ProcessingTaskDto createTask(ProcessingTaskDto dto) {
        if (dto.companyId == null || dto.companyId.isEmpty()) {
            throw new ApiException(400, "companyId is required");
        }

        Company company = companyRepo.findById(dto.companyId)
                .orElseThrow(() -> new ApiException(404, "Company not found: " + dto.companyId));

        // 새로운 ProcessingTask 생성
        ProcessingTask newTask = new ProcessingTask();
        newTask.id = new ObjectId();
        newTask.companyId = dto.companyId;
        newTask.taskName = dto.taskName;
        newTask.purpose = dto.purpose;
        newTask.department = dto.department;
        newTask.infomation = dto.infomation;
        newTask.createdAt = Instant.now();
        newTask.updatedAt = Instant.now();

        // Company의 processingTasks 배열에 추가
        if (company.processingTasks == null) {
            company.processingTasks = new ArrayList<>();
        }
        company.processingTasks.add(newTask);
        company.updatedAt = Instant.now();

        // 저장
        companyRepo.save(company);

        return toDto(newTask);
    }

    /**
     * 처리업무 일괄 저장 (생성 + 수정)
     * 프론트엔드에서 전체 배열을 보내면, 기존 항목은 업데이트하고 새 항목은 생성
     */
    public void bulkSave(String companyId, List<ProcessingTaskDto> dtoList) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "Company not found: " + companyId));

        if (company.processingTasks == null) {
            company.processingTasks = new ArrayList<>();
        }

        Instant now = Instant.now();

        for (ProcessingTaskDto dto : dtoList) {
            if (dto.id == null || dto.id.isEmpty() || dto.id.startsWith("temp_")) {
                // 새로운 항목 생성
                ProcessingTask newTask = new ProcessingTask();
                newTask.id = new ObjectId();
                newTask.companyId = companyId;
                newTask.taskName = dto.taskName;
                newTask.purpose = dto.purpose;
                newTask.department = dto.department;
                newTask.infomation = dto.infomation;
                newTask.createdAt = now;
                newTask.updatedAt = now;

                company.processingTasks.add(newTask);
            } else {
                // 기존 항목 수정
                ObjectId taskId = new ObjectId(dto.id);
                Optional<ProcessingTask> existingTaskOpt = company.processingTasks.stream()
                        .filter(t -> t.id.equals(taskId))
                        .findFirst();

                if (existingTaskOpt.isPresent()) {
                    ProcessingTask existingTask = existingTaskOpt.get();
                    existingTask.taskName = dto.taskName;
                    existingTask.purpose = dto.purpose;
                    existingTask.department = dto.department;
                    existingTask.infomation = dto.infomation;
                    existingTask.updatedAt = now;
                }
            }
        }

        company.updatedAt = now;
        companyRepo.save(company);
    }

    /**
     * 처리업무 수정
     */
    public ProcessingTaskDto updateTask(String taskIdStr, ProcessingTaskDto dto) {
        ObjectId taskId = new ObjectId(taskIdStr);

        if (dto.companyId == null || dto.companyId.isEmpty()) {
            throw new ApiException(400, "companyId is required");
        }

        Company company = companyRepo.findById(dto.companyId)
                .orElseThrow(() -> new ApiException(404, "Company not found: " + dto.companyId));

        if (company.processingTasks == null) {
            throw new ApiException(404, "No processing tasks found");
        }

        // 해당 taskId를 가진 항목 찾기
        Optional<ProcessingTask> taskOpt = company.processingTasks.stream()
                .filter(t -> t.id.equals(taskId))
                .findFirst();

        if (taskOpt.isEmpty()) {
            throw new ApiException(404, "Processing task not found: " + taskIdStr);
        }

        // 수정
        ProcessingTask task = taskOpt.get();
        task.taskName = dto.taskName;
        task.purpose = dto.purpose;
        task.department = dto.department;
        task.infomation = dto.infomation;
        task.updatedAt = Instant.now();

        company.updatedAt = Instant.now();

        // 저장
        companyRepo.save(company);

        return toDto(task);
    }

    /**
     * 처리업무 삭제
     */
    public boolean deleteTask(String taskIdStr) {
        ObjectId taskId = new ObjectId(taskIdStr);

        // 모든 회사를 조회하여 해당 task 찾기
        List<Company> allCompanies = companyRepo.findAll();

        for (Company company : allCompanies) {
            if (company.processingTasks == null || company.processingTasks.isEmpty()) {
                continue;
            }

            boolean removed = company.processingTasks.removeIf(t -> t.id.equals(taskId));

            if (removed) {
                company.updatedAt = Instant.now();
                companyRepo.save(company);
                return true;
            }
        }

        return false;
    }

    /**
     * ProcessingTask -> ProcessingTaskDto 변환
     */
    private ProcessingTaskDto toDto(ProcessingTask task) {
        ProcessingTaskDto dto = new ProcessingTaskDto();
        dto.id = task.id.toHexString();
        dto.taskName = task.taskName;
        dto.purpose = task.purpose;
        dto.department = task.department;
        dto.infomation = task.infomation;
        dto.companyId = task.companyId;
        return dto;
    }
}