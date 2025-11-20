package com.epia.dto;

public class FlowChartImageDto {
    private String fileName;
    private String imageUrl;

    public FlowChartImageDto() {
    }

    public FlowChartImageDto(String fileName, String imageUrl) {
        this.fileName = fileName;
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}