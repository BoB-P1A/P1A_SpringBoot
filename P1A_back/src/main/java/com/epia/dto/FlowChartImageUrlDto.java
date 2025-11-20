package com.epia.dto;

public class FlowChartImageUrlDto {
    private String url;
    private String fileName;

    public FlowChartImageUrlDto() {
    }

    public FlowChartImageUrlDto(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}