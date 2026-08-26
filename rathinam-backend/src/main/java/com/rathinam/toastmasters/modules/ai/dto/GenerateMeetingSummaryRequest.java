package com.rathinam.toastmasters.modules.ai.dto;

public class GenerateMeetingSummaryRequest {

    private String focusArea;
    private String tone;

    public GenerateMeetingSummaryRequest() {
    }

    public GenerateMeetingSummaryRequest(String focusArea, String tone) {
        this.focusArea = focusArea;
        this.tone = tone;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}
