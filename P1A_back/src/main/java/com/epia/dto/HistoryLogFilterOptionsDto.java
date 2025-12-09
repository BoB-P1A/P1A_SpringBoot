package com.epia.dto;

import java.util.List;

public class HistoryLogFilterOptionsDto {
    public List<String> areas;
    public List<String> targetNames;
    public List<String> changedByNames;

    public HistoryLogFilterOptionsDto() {}

    public HistoryLogFilterOptionsDto(List<String> areas, List<String> targetNames, List<String> changedByNames) {
        this.areas = areas;
        this.targetNames = targetNames;
        this.changedByNames = changedByNames;
    }
}